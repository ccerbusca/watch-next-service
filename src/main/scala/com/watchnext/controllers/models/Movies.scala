package com.watchnext.controllers.models

object Movies {

  final case class Movie(
    name: String,
    id: String
  )

  final case class MovieId(
    id: String
  )

}
