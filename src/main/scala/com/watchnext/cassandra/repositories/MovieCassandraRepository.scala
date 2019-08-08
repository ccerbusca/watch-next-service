package com.watchnext.cassandra.repositories

import com.outworkers.phantom.dsl._
import com.watchnext.movies.domain.Movies.{Movie, MovieId}
import com.watchnext.movies.repository.MovieRepository

import scala.concurrent.Future

class MovieCassandraRepository(connector: CassandraConnection)
  extends MovieRepository {

  override def store(movie: Movie): Future[MovieId] = ???

  override def retrieve(id: MovieId): Future[Movie] = ???

}

object MovieCassandraRepository {

  private val SSTableSizeInMb: Int = 50

  trait

}
