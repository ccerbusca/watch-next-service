package com.watchnext.controllers

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import com.watchnext.controllers.models.Movies._
import com.watchnext.movies.service.MovieHandlerService
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext.Implicits.global

object MovieController extends DefaultJsonProtocol {

  import com.watchnext.controllers.json.Movies._
  import akka.http.scaladsl.model.StatusCodes._

  val addRoute = path("/add") {
    post {
      entity(as[Movie]) { movie =>
        complete(OK)
      }
    } ~
    path("/latest") {
      val movieService = new MovieHandlerService(
        new
      )
    }
  }

}
