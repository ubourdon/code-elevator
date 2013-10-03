import sbt._
import sbt.Keys._

object ApplicationBuild extends Build {

    val appName = "code-elevator"
    val appVersion = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "org.scalatest" %% "scalatest" % "1.9.1" % "test",
        "org.mockito" % "mockito-all" % "1.9.0" % "test",
        "com.typesafe.akka" %% "akka-testkit" % "2.2.1" % "test",
        "com.jayway.awaitility" % "awaitility-scala" % "1.3.5",
        "com.github.simplyscala" %% "simplyscala-server" % "0.5" % "test"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(

    )
}