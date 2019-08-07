package com.watchnext.controllers

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import com.watchnext.controllers.models.Movies._
import spray.json.DefaultJsonProtocol

object MovieController extends DefaultJsonProtocol {

  import com.watchnext.controllers.json._

  val addRoute = path("/add") {
    post {
      entity(as[Movie]) { movie =>

      }
    }
  }

}
