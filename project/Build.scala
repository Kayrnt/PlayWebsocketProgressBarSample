import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "WSProgressBar"
  val appVersion      = "0.1"

  val appDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.2.1",
    "com.typesafe.akka" %% "akka-slf4j" % "2.2.1",
    "com.typesafe.akka" %% "akka-testkit" % "2.2.1" % "test"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
