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

package com.watchnext.cassandra.repositories

import com.datastax.driver.core.SocketOptions
import com.outworkers.phantom.connectors.{CassandraConnection, KeySpace}
import com.outworkers.phantom.dsl._

object CassandraService {

  import com.watchnext.ServiceConfig._

  def connection: CassandraConnection = {
    import scala.collection.JavaConverters._
    ContactPoints(cassandraSeedNodes.asScala)
      .withClusterBuilder(
        _.withSocketOptions(
          new SocketOptions()
            .setConnectTimeoutMillis(cassandraConnectionTimeout)
            .setReadTimeoutMillis(cassandraReadTimeout)
        )
      )
      .noHeartbeat()
      .keySpace(
        KeySpace("watchnext")
          .ifNotExists()
          .`with`(
            replication eqs SimpleStrategy.replication_factor(1)
          )
          .and(durable_writes eqs true)
      )
  }

}
