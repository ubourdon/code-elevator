import sbt._
import sbt.Keys._

object ApplicationBuild extends Build {

    val appName = "code-elevator"
    val appVersion = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "org.scalatest" %% "scalatest" % "1.9.1" % "test",
        "org.mockito" % "mockito-all" % "1.9.0" % "test"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(

        testOptions in Test := Nil, //to run scalatest in play2 console arghhhh!!!

        // available test resources in play2 classpath
        unmanagedClasspath in Test <+= ( baseDirectory ) map {
            bd => Attributed.blank(bd / "test")
        }
    )
}