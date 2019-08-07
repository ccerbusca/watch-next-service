package com.watchnext.movies.domain

object Movies {

  type ErrorMsg = String
  type MovieId = String

  final case class Name private (value: String) extends AnyVal

  final case class Movie(
    title: Name,
    id: MovieId
  )

  object Name {

    def apply(value: String): Either[ErrorMsg, Name] = {
      if (isValid(value)) {
        Right(new Name(value))
      } else {
        Left("Not valid name")
      }
    }

    private def isValid(value: String): Boolean = {
      value.length <= 50
    }

  }

}
