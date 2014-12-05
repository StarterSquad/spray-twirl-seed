import com.typesafe.sbt.SbtStartScript
import sbt._
import sbt.Keys._
import spray.revolver.RevolverPlugin._
import twirl.sbt.TwirlPlugin._

object BuildSettings {
  val buildOrganization = "com.ssq"
  val buildVersion = "0.1"
  val buildScalaVersion = "2.11.4"

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    version := buildVersion,
    scalaVersion := buildScalaVersion,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-Xlint", "-Xfatal-warnings"),
    javaOptions += "-Xmx1G",
    shellPrompt := ShellPrompt.buildShellPrompt,
    updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)
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
  val sprayVersion = "1.3.2"
  val akkaVersion = "2.3.6"

  val spray = Seq(
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-client" % sprayVersion,
    "io.spray" %% "spray-testkit" % sprayVersion % "test"
  )

  val akka = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  )

  val logging = Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "org.slf4j" % "slf4j-api" % "1.7.7",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2"
  )

  val testing = Seq(
    "org.specs2" %% "specs2" % "2.3.12" % "test,it",
    "org.specs2" %% "specs2-mock" % "2.3.12" % "test,it",
    "org.specs2" %% "specs2-matcher-extra" % "2.3.12" % "test,it",
    "org.mockito" % "mockito-core" % "1.9.5" % "test,it"
  )

  val security = Seq(
    "org.mindrot" % "jbcrypt" % "0.3m"
  )

  val mail = Seq(
    "org.apache.commons" % "commons-email" % "1.3.2",
    "com.icegreen" % "greenmail" % "1.3.1b" % "test,it"
  )

  val others = Seq(
    "com.gilt" % "handlebars_2.10" % "0.0.19-20131031112233" exclude("org.slf4j", "slf4j-simple"),
    "commons-io" % "commons-io" % "2.4",
    "commons-codec" % "commons-codec" % "1.9"
  )


  val dependencies = spray ++ akka ++ logging ++ testing ++ security ++ mail ++ others
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

  lazy val ssqApi = Project(
    "ssq-api", file("."),
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
