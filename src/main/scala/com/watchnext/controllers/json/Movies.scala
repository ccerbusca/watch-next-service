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

package com.watchnext.controllers.json

import com.watchnext.controllers.models.Movies.{Movie, MovieIDs, MovieId}
import spray.json.DefaultJsonProtocol._
object Movies {

  implicit val movieIDsFormat = jsonFormat1(MovieIDs)
  implicit val movieFormat    = jsonFormat2(Movie)
  implicit val movieIdFormat  = jsonFormat1(MovieId)

}
