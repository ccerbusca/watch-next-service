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

package com.watchnext.movies.domain

import java.net.URL

import scala.util.Try

object Movies {

  type ErrorMsg = String
  type MovieId  = String

  final case class Name private (value: String) extends AnyVal
  final case class Link private (value: String) extends AnyVal

  final case class Movie(
      id: MovieId,
      title: Name,
      link: Link,
      watched: Boolean
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

  object Link {
    def apply(value: String): Either[ErrorMsg, Link] =
      Try {
        new URL(value)
      } fold (error => Left(error.getMessage), _ => Right(new Link(value)))
  }

}
