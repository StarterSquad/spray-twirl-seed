import com.typesafe.sbt.SbtStartScript
import scala._
import sbt._
import sbt.Keys._
import spray.revolver.RevolverPlugin._
import twirl.sbt.TwirlPlugin._

object BuildSettings {
  val buildOrganization = "com.xebia"
  val buildVersion = "0.1"
  val buildScalaVersion = "2.10.1"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    scalacOptions ++= Seq( "-unchecked", "-deprecation", "-encoding", "utf8"),
    javaOptions += "-Xmx1G",
    shellPrompt := ShellPrompt.buildShellPrompt
  )
}

object Resolvers {
  val typesafe = "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
  val spray = "Spray repo" at "http://repo.spray.io/"
  val akka = "Akka repo" at "http://repo.akka.io/releases/"
  val sonatypeSnapshots = "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
  val sonatypeReleases = "releases"  at "http://oss.sonatype.org/content/repositories/releases"
  val repositories = Seq(typesafe, spray, akka, sonatypeSnapshots, sonatypeReleases)
}


object Dependencies {
  val sprayVersion = "1.1-M7"
  val akkaVersion = "2.1.2"

  val spray = Seq(
    "io.spray" % "spray-can" % sprayVersion,
    "io.spray" % "spray-routing" % sprayVersion,
    "io.spray" %% "spray-json" % "1.2.3",
    "io.spray" % "spray-client" % sprayVersion,
    "io.spray" % "spray-testkit" % sprayVersion
  )

  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  )

  val logging = Seq(
    "ch.qos.logback" % "logback-classic" % "1.0.7",
    "org.slf4j" % "slf4j-api" % "1.6.1",
    "com.weiglewilczek.slf4s" % "slf4s_2.9.1" % "1.0.7"
  )

  val testing = Seq(
    "org.specs2" %% "specs2" % "1.12.3" % "test,it",
    "org.mockito" % "mockito-core" % "1.9.5" % "test,it"
  )

  val db = Seq(
    "org.mongodb" %% "casbah" % "2.5.0",
    "com.novus" %% "salat" % "1.9.4"
  )

  val security = Seq(
    "com.google.api-client" % "google-api-client" % "1.14.1-beta",
    "com.google.http-client" % "google-http-client-jackson2" % "1.14.1-beta",
    "com.google.apis" % "google-api-services-oauth2" % "v2-rev36-1.14.2-beta"
  )

  val dependencies = spray ++ akka ++ logging ++ testing ++ db
}

/**
 * Revolver.settings: (https://github.com/spray/sbt-revolver) Allows for hot reloading when JRebel is configured.
 * Integration tests should end with 'IT'. Run it:test to run integration tests only.
 * Unit tests must end with 'Spec' or 'Test'
 */
object ThisBuild extends Build {

  import BuildSettings._
  import Resolvers._
  import Dependencies._

  lazy val mmoigo = Project(
    "your-project-name-goes-here", file("."),
    settings = buildSettings
      ++ Seq(resolvers := repositories, libraryDependencies ++= dependencies)
      ++ SbtStartScript.startScriptForClassesSettings
      ++ Revolver.settings
      ++ Twirl.settings
  ).configs( IntegrationTest)
    .settings( Defaults.itSettings : _*)
    .settings( parallelExecution in Test := false)
    .settings( scalaSource in IntegrationTest <<= baseDirectory / "src/test/scala")
    .settings( resourceDirectory in IntegrationTest <<= baseDirectory / "src/test/resources")
    .settings( testOptions in IntegrationTest := Seq(Tests.Filter(s => s.endsWith("IT"))))
}

// Shell prompt which show the current project,
// git branch and build version
object ShellPrompt {

  object devnull extends ProcessLogger {
    def info(s: => String) {}

    def error(s: => String) {}

    def buffer[T](f: => T): T = f
  }

  def currBranch = (
    ("git status -sb" lines_! devnull headOption)
      getOrElse "-" stripPrefix "## "
    )

  val buildShellPrompt = {
    (state: State) => {
      val currProject = Project.extract(state).currentProject.id
      "%s:%s:%s> ".format(
        currProject, currBranch, BuildSettings.buildVersion
      )
    }
  }
}
