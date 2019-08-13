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

package com.watchnext

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.watchnext.controllers.MovieController
import scribe.Logging

import scala.concurrent.Future

object Boot extends Logging {

  def main(args: Array[String]): Unit = {

    logger.info(s"[Watch-Next] started")

    implicit val system            = ActorSystem("Watch-Next")
    implicit val actorMaterializer = ActorMaterializer()
    implicit val executionContext  = system.dispatcher

    logger.info(s"[Watch-Next] ${ServiceConfig.webBindingInterface} ${ServiceConfig.webBindingPort}")

    val routes = MovieController.routes
    val binding: Future[Http.ServerBinding] =
      Http().bindAndHandle(routes, ServiceConfig.webBindingInterface, ServiceConfig.webBindingPort)
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run() {
        logger.info(s"[Watch-Next] shutting down ....")
        binding
          .flatMap(_.unbind())                 // trigger unbinding from the port
          .onComplete(_ => system.terminate()) // and shutdown when done
      }
    })
  }

}
