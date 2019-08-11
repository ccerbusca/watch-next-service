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

package com.watchnext.movies.service

import com.watchnext.ServiceConfig
import com.watchnext.movies.domain.Movies.{Movie, MovieId}
import com.watchnext.movies.repository.MovieRepository

import scala.concurrent.{ExecutionContext, Future}

class MovieHandlerService(
    movieRepository: MovieRepository
)(implicit executionContext: ExecutionContext)
    extends MovieService {

  override def findById(id: MovieId): Future[Movie] =
    movieRepository.retrieve(id)

  private def getJsonResponse(url: String): String = {
    val urlSource    = scala.io.Source.fromURL(url)
    val jsonResponse = urlSource.mkString
    urlSource.close
    jsonResponse
  }

  def latestMovies: String =
    getJsonResponse(s"https://api.themoviedb.org/3/movie/latest?api_key=${ServiceConfig.apiKey}&language=en-US")

  def details(id: String): String =
    getJsonResponse(s"https://api.themoviedb.org/3/movie/$id?api_key=${ServiceConfig.apiKey}")

  def search(query: String): String = {
    import spray.json._
    import DefaultJsonProtocol._
    val searchResult = getJsonResponse(
      s"https://api.themoviedb.org/3/search/movie?query=$query&api_key=${ServiceConfig.apiKey}&language=en-US"
    ).parseJson.asJsObject
    val newResult = searchResult.fields.collect {
      case ("id", value) => "id" -> value
      case ("title", value) => "title" -> value
    }.toJson.asJsObject
    JsObject(
      newResult.fields + ("link" -> JsString(s"https://www.themoviedb.org/movie/${searchResult.fields("id")}"))
    ).toString
  }

  def suggestions: Future[String] = {
    import spray.json._

    movieRepository.getAll.map { movies =>
      val watchedMovies = movies.filter(_.watched)
      val genreIDs = watchedMovies.flatMap { movie =>
        details(movie.id)
          .parseJson.asJsObject
          .fields("genres").asInstanceOf[JsArray]
          .elements.map(_.asJsObject.fields("id").toString)
      } groupBy identity mapValues(_.size)
      val topTwoGenres = {
        val first = genreIDs.maxBy(_._2)
        val second = (genreIDs - first._1).maxBy(_._2)
        List(first._1, second._1)
      }.mkString(",")
      getJsonResponse(
        s"https://api.themoviedb.org/3/discover/movie?api_key=${ServiceConfig.apiKey}" +
          s"&language=en-US&sort_by=popularity.desc&with_genres=$topTwoGenres"
      )
    }
  }

  override def addMovie(movie: Movie): Future[MovieId] = movieRepository.store(movie)
}
