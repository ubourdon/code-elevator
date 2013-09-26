import sbt._
import sbt.Keys._

object ApplicationBuild extends Build {

    val appName = "code-elevator"
    val appVersion = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "org.scalatest" %% "scalatest" % "1.9.1" % "test",
        "org.mockito" % "mockito-all" % "1.9.0" % "test",
        "com.typesafe.akka" %% "akka-actor-tests" % "2.2.1" % "test",
        "com.typesafe.akka" %% "akka-testkit" % "2.2.1" % "test"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(

    )
}