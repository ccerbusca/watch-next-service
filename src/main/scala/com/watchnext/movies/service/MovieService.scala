package com.watchnext.movies.service

import com.watchnext.movies.domain.Movies.{Movie, MovieId}

import scala.concurrent.Future

trait MovieService {

  def findById(id: MovieId): Future[Movie]

}
