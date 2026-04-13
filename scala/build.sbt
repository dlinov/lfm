enablePlugins(GraalVMNativeImagePlugin)

name := "lfm"

scalaVersion := "3.8.1"

// Compile / run / fork := true

val circeVersion = "0.14.14"
val http4sVersion = "0.23.33"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
)

graalVMNativeImageOptions ++= Seq(
  "--enable-https",
  "--initialize-at-build-time",
  "--no-fallback",
  "--diagnostics-mode",
  "--gc=G1",
  "-H:+ReportExceptionStackTraces",
  "-H:+UnlockExperimentalVMOptions",
  "-march=native",
)

// trapExit := false
