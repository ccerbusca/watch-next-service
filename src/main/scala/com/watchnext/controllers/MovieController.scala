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

package com.watchnext.controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.watchnext.cassandra.repositories.{CassandraService, MovieCassandraRepository}
import com.watchnext.controllers.models.Movies._
import com.watchnext.movies.domain.Movies
import com.watchnext.movies.service.MovieHandlerService
import scribe.Logging
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext.Implicits.global

object MovieController extends DefaultJsonProtocol with Logging {

  import com.watchnext.controllers.json.Movies._

  val routes: Route =
    path("latest") {
      get {
        val movieService = new MovieHandlerService(
          new MovieCassandraRepository(CassandraService.connection)
        )
        complete(movieService.latestMovies)
      }
    } ~
      path("add") {
        post {
          entity(as[Movie]) { movie =>
            val movieService = new MovieHandlerService(
              new MovieCassandraRepository(CassandraService.connection)
            )
            val maybeMovie = for {
              title <- Movies.Name(movie.title)
              link  <- Movies.Link(s"https://www.themoviedb.org/movie/${movie.id}")
            } yield Movies.Movie(movie.id.toString, title, link, watched = false)
            maybeMovie.fold(
              _ => complete(StatusCodes.BadRequest),
              movie => complete(movieService.addMovie(movie))
            )
          }
        }
      } ~
      path("details") {
        post {
          entity(as[MovieIDs]) { movieIDs =>
            val movieService = new MovieHandlerService(
              new MovieCassandraRepository(CassandraService.connection)
            )
            val result = s"""{"result":[${movieIDs.ids.map(id => movieService.details(id.toString)).mkString(",")}]}"""
            complete(result)
          }
        }
      } ~
      path("details" / Segment) { id =>
        get {
          val movieService = new MovieHandlerService(
            new MovieCassandraRepository(CassandraService.connection)
          )
          complete(movieService.details(id))
        }
      } ~
      path("search") {
        get {
          parameters('q.?) { text: Option[String] =>
            val movieService = new MovieHandlerService(
              new MovieCassandraRepository(CassandraService.connection)
            )
            text.fold(complete(StatusCodes.BadRequest))(query => complete(movieService.search(query)))
          }
        }
      } ~
      path("suggestions") {
        get {
          val movieService = new MovieHandlerService(
            new MovieCassandraRepository(CassandraService.connection)
          )
          complete(movieService.suggestions)
        }
      } ~
      path("setWatched" / Segment) { id =>
        patch {
          val movieService = new MovieHandlerService(
            new MovieCassandraRepository(CassandraService.connection)
          )
          movieService.setAsWatched(id)
          complete(StatusCodes.OK)
        }
      }

}
