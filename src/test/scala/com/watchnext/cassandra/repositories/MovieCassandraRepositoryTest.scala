package com.watchnext.cassandra.repositories

import com.dimafeng.testcontainers.{CassandraContainer, ForAllTestContainer}
import com.outworkers.phantom.dsl.{ContactPoint, KeySpace, _}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers, OptionValues}

class MovieCassandraRepositoryTest
  extends FlatSpec
    with Matchers
    with ScalaFutures
    with BeforeAndAfterAll
    with OptionValues
    with ForAllTestContainer {

  override val container: CassandraContainer = CassandraContainer("spotify/cassandra:latest")

  private val defaultCassandraPort: Int = 9042

  lazy val connector =
    ContactPoint(container.containerIpAddress, container.mappedPort(defaultCassandraPort))
      .noHeartbeat()
      .keySpace(
        KeySpace("WatchNext")
          .ifNotExists()
          .`with`(
            replication eqs SimpleStrategy.replication_factor(1)
          )
          .and(durable_writes eqs true)
      )

  lazy val movieCassandraRepository = new MovieCassandraRepository(connector)

  override def beforeAll(): Unit = {
    movieCassandraRepository.database.create()
  }

  override def afterAll(): Unit = {
    movieCassandraRepository.database.truncate()
  }

  it should "store a movie and be able to retrieve it by id" in {
    import MovieCassandraRepository._

    val rawMovie = RawMovie("123", "Bla Bla", "https://google.com", watched = false)
    val probe = rawMovie.toModel

    val result = probe match {
      case Left(errorMsg) =>
        fail(s"Failed to generate a valid Movie instance: $errorMsg")
      case Right(movie) =>
        for {
          id <- movieCassandraRepository.store(movie)
          retrieved <- movieCassandraRepository.retrieve(id)
        } yield retrieved == movie
    }

    whenReady(result) { areEqual =>
      assert(areEqual, true)
    }


  }

  it should "retrieve all movies from the database" in {
    import MovieCassandraRepository._

    val rawMovie1 = RawMovie("123", "Bla Bla", "https://google.com", watched = false)
    val rawMovie2 = RawMovie("1234", "Bla Bla Bla", "https://google.com", watched = true)

    val probe1 = rawMovie1.toModel
    val probe2 = rawMovie2.toModel

    val result = (probe1, probe2) match {
      case (Left(errorMsg), _) =>
        fail(s"Failed to create an instance for teh first movie: $errorMsg")

      case (_, Left(errorMsg)) =>
        fail(s"Failed to create an instance for teh first movie: $errorMsg")

      case (Right(movie1), Right(movie2)) =>
        for {
          id1 <- movieCassandraRepository.store(movie1)
          id2 <- movieCassandraRepository.store(movie2)
          all <- movieCassandraRepository.getAll
        } yield {
          all.exists(_.id == id1) && all.exists(_.id == id2)
        }

    }

    whenReady(result) { exists =>
      exists shouldBe true
    }

  }

  it should "set movie as watched" in {
    import MovieCassandraRepository._

    val rawMovie = RawMovie("123", "Bla Bla", "https://google.com", watched = false)
    val probe = rawMovie.toModel

    val result = probe match {
      case Left(errorMsg) =>
        fail(s"Failed to generate a valid Movie instance: $errorMsg")
      case Right(movie) =>
        for {
          id <- movieCassandraRepository.store(movie)
          setAsWatched <- movieCassandraRepository.setWatched(id)
          retrieved <- movieCassandraRepository.retrieve(id)
        } yield setAsWatched && retrieved.watched
    }

    whenReady(result) { setAsWatched =>
      setAsWatched shouldBe true
    }
  }




}
