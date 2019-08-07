package com.watchnext.movies.repository

import com.watchnext.movies.domain.Movies.{Movie, MovieId}

import scala.concurrent.Future

trait MovieRepository {

  def store(movie: Movie): Future[MovieId]

  def retrieve(id: MovieId): Future[Movie]

}
