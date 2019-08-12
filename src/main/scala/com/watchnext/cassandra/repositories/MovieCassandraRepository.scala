/*
 * Copyright 2019 Metro Systems Scala School
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.watchnext.cassandra.repositories

import com.outworkers.phantom.builder.Unspecified
import com.outworkers.phantom.builder.query.CreateQuery
import com.outworkers.phantom.dsl._
import com.watchnext.cassandra.repositories.MovieCassandraRepository._
import com.watchnext.movies.domain.Movies
import com.watchnext.movies.domain.Movies.{ErrorMsg, Movie, MovieId}
import com.watchnext.movies.repository.MovieRepository

import scala.concurrent.Future

class MovieCassandraRepository(connector: CassandraConnection) extends MovieRepository with MovieDatabaseProvider {

  override def store(movie: Movie): Future[MovieId] = {
    val rawMovie = RawMovie(
      movie.id,
      movie.title.value,
      movie.link.value,
      movie.watched
    )
    for {
      _ <- db.Movies.storeRecord(rawMovie)
    } yield movie.id
  }

  override def retrieve(id: MovieId): Future[Movie] =
    db.Movies
      .findByID(id)
      .map(_.flatMap(_.toModel.toOption))
      .flatMap {
        case Some(movie) => Future.successful(movie)
        case None        => Future.failed(new Exception(s"Could not retrieve movie with id: $id"))
      }

  override def getAll: Future[List[Movie]] =
    db.Movies.getAll
      .map(
        _.map { rawMovie =>
          for {
            title <- Movies.Name(rawMovie.title)
            link  <- Movies.Link(rawMovie.link)
          } yield Movies.Movie(rawMovie.id, title, link, rawMovie.watched)
        } collect { case Right(movie) => movie }
      )

  override def setWatched(id: MovieId): Future[Boolean] =
    for {
      watched <- db.Movies.setAsWatched(id)
    } yield watched.wasApplied

  override def database: MovieDatabase = new MovieDatabase(connector)
}

object MovieCassandraRepository {

  private val SSTableSizeInMb: Int = 50

  trait MovieDatabaseProvider extends DatabaseProvider[MovieDatabase]

  implicit class MovieWrapper(rawMovie: RawMovie) {

    def toModel: Either[ErrorMsg, Movie] =
      for {
        link  <- Movies.Link(rawMovie.link)
        title <- Movies.Name(rawMovie.title)
      } yield Movie(rawMovie.id, title, link, rawMovie.watched)

  }

  case class RawMovie(
      id: String,
      title: String,
      link: String,
      watched: Boolean
  )

  abstract class MovieTable extends Table[MovieTable, RawMovie] {
    object Link    extends StringColumn
    object Title   extends StringColumn
    object Id      extends StringColumn with PartitionKey
    object Watched extends BooleanColumn

    def findByID(id: String): Future[Option[RawMovie]] = {
      select.where(_.Id eqs id).one()
    }

    def setAsWatched(id: String): Future[ResultSet] = {
      update().where(_.Id eqs id).modify(_.Watched.setTo(true)).future()
    }

    def getAll: Future[List[RawMovie]] = {
      select.all.fetch()
    }

  }

  class MovieDatabase(override val connector: CassandraConnection) extends Database[MovieDatabase](connector) {

    object Movies extends MovieTable with Connector {
      override def autocreate(
          keySpace: KeySpace
      ): CreateQuery[MovieTable, RawMovie, Unspecified] = {
        create
          .ifNotExists()(keySpace)
          .`with`(compaction eqs LeveledCompactionStrategy.sstable_size_in_mb(SSTableSizeInMb))
          .and(compression eqs LZ4Compressor.crc_check_chance(0.5))
          .and(comment eqs "testing")
          .and(read_repair_chance eqs 1d)
          .and(dclocal_read_repair_chance eqs 1d)

      }
    }

  }

}
