val Http4sVersion = "0.20.3"
val CirceVersion = "0.11.1"
val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .settings(
    organization := "com.ynap",
    name := "mru",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    scalacOptions ++= Seq("-Ypartial-unification"),
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % Http4sVersion,
      "org.http4s" %% "http4s-circe" % Http4sVersion,
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      
      "io.circe" %% "circe-core" % CirceVersion,
      "io.circe" %% "circe-generic" % CirceVersion,
      "io.circe" %% "circe-parser" % CirceVersion,
      "io.circe" %% "circe-literal" % CirceVersion,
      
      "org.scalatest" %% "scalatest" % "3.0.8" % "test",
      "ch.qos.logback" % "logback-classic" % LogbackVersion
    ),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4")
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Ypartial-unification",
  "-Xfatal-warnings",
  "-unchecked",
  "-explaintypes",
  "-opt-warnings",
  "-language:existentials",
  "-opt:l:inline",
  "-opt-inline-from:<source>",
  "-Yrangepos",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-extra-implicit",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Xsource:2.13",
  "-Xlint:_,-type-parameter-shadow,-unused",
  "-Xfuture",
)

addCommandAlias("fm", "all compile:scalafmt test:scalafmt")
addCommandAlias("cc", "all clean compile")
addCommandAlias("c", "compile")
addCommandAlias("r", "run")
addCommandAlias("t", "test")
addCommandAlias("to", "testOnly")
addCommandAlias("ps", "projects")
addCommandAlias("p", "project")
