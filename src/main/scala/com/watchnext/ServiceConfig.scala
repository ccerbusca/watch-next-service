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

import java.util

import com.typesafe.config.ConfigFactory

object ServiceConfig {
  lazy val conf = ConfigFactory.load()

  val apiKey = conf.getString("themoviedb.apikey")

  val webBindingPort      = conf.getInt("web.binding.port")
  val webBindingInterface = conf.getString("web.binding.interface")

  val cassandraSeedNodes: util.List[String] = conf.getStringList("cassandra.seed.nodes")
  val cassandraConnectionTimeout            = conf.getInt("cassandra.connection.timeout")
  val cassandraReadTimeout                  = conf.getInt("cassandra.read.timeout")
}
