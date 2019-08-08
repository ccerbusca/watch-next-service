package com.watchnext.movies.service

import com.watchnext.ServiceConfig
import com.watchnext.movies.domain.Movies.{Movie, MovieId}
import com.watchnext.movies.repository.MovieRepository

import scala.concurrent.{ExecutionContext, Future}

class MovieHandlerService(
  movieRepository: MovieRepository
)(implicit executionContext: ExecutionContext) extends MovieService {

  override def findById(id: MovieId): Future[Movie] =
    movieRepository.retrieve(id)

  def latestMovie: String = {
    val urlSource = scala.io.Source.fromURL(
      s"https://api.themoviedb.org/3/movie/latest?api_key=${ServiceConfig.apiKey}&language=en-US"
    )
    val jsonResponse = urlSource.mkString
    urlSource.close
    jsonResponse
  }

}
