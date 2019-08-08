package com.watchnext

import java.util

import com.typesafe.config.ConfigFactory

object ServiceConfig {
  lazy val conf = ConfigFactory.load()

  val apiKey = conf.getString("themoviedb.apikey")

  val cassandraSeedNodes: util.List[String] = conf.getStringList("cassandra.seed.nodes")
  val cassandraConnectionTimeout            = conf.getInt("cassandra.connection.timeout")
  val cassandraReadTimeout                  = conf.getInt("cassandra.read.timeout")
}
