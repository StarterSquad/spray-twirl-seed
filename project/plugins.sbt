addSbtPlugin("com.typesafe.sbt" % "sbt-start-script" % "0.6.0")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.6.2")

addSbtPlugin("io.spray" % "sbt-twirl" % "0.6.1")

resolvers ++= Seq(
    "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/"
)