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
        KeySpace("eshop")
          .ifNotExists()
          .`with`(
            replication eqs SimpleStrategy.replication_factor(1)
          )
          .and(durable_writes eqs true)
      )
  }

}
