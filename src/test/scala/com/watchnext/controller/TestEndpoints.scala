package com.watchnext.controller

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.watchnext.controllers.MovieController
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class TestEndpoints extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  val routes: Route = MovieController.routes

  "The controller" should {

    "return an OK status code" in {
      Get("/latest") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    "successfully add a movie" in {
      val jsonRequest = ByteString("{\"id\":\"123\", \"title\":\"test\", \"link\": \"https://google.com\"}")

      val postRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/add",
        entity = HttpEntity(MediaTypes.`application/json`, jsonRequest)
      )


      postRequest ~> routes ~> check {
        responseAs[String] shouldEqual "123"
      }
    }

  }

}
