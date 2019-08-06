import sbtrelease.ReleaseStateTransformations._

name := """watch-next--service"""
organization := "Metro Systems Scala School"
startYear := Some(2019)

scalaVersion := "2.12.8"

lazy val `watch-next-service` = (project in file("."))
  .enablePlugins(AutomateHeaderPlugin, JavaAppPackaging, BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.eshop"
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
//libraryDependencies += "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.0" % Test
libraryDependencies += "com.outr"      %% "scribe"    % "2.7.3"
libraryDependencies += "com.googlecode.libphonenumber" % "libphonenumber" % "4.3"
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.22.0"
libraryDependencies ++= Seq(
  "com.outworkers"  %% "phantom-dsl" % "2.42.0"
)
libraryDependencies += "com.dimafeng" %% "testcontainers-scala" % "0.28.0" % "test"
libraryDependencies += "org.testcontainers" % "cassandra" % "1.11.3" % "test"

libraryDependencies += "com.typesafe.akka" %% "akka-http"   % "10.1.8"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.23"

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.1"
libraryDependencies += "org.typelevel" %% "cats-effect" % "1.3.1"


scalafmtOnCompile := true

dockerExposedPorts ++= Seq(9000)
// scala style during compile

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := scalastyle.in(Compile).toTask("").value
(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value

// header license
licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

// release process
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // : ReleaseStep
  inquireVersions,           // : ReleaseStep
  runClean,
  runTest,              // : ReleaseStep
  setReleaseVersion,    // : ReleaseStep
  commitReleaseVersion, // : ReleaseStep, performs the initial git checks
  tagRelease,           // : ReleaseStep
  //publishArtifacts, // : ReleaseStep, checks whether `publishTo` is properly set up
  ReleaseStep(releaseStepTask(publishLocal in Docker)),
  setNextVersion,    // : ReleaseStep
  commitNextVersion, // : ReleaseStep
  pushChanges        // : ReleaseStep, also checks that an upstream branch is properly configured
)
