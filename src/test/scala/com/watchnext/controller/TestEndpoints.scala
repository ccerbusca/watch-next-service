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

package com.watchnext.controller

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import com.watchnext.controllers.MovieController
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class TestEndpoints extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with BeforeAndAfterAll {

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

    "succesfully search for a movie" in {
      Get("/search?q=shawshank") ~> routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

  }

}
