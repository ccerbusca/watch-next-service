package com.watchnext.controllers.json

import com.watchnext.controllers.models.Movies.{Movie, MovieId}
import spray.json.DefaultJsonProtocol._
object Movies {

  implicit val movieFormat = jsonFormat2(Movie)
  implicit val movieIdFormat = jsonFormat1(MovieId)

}
