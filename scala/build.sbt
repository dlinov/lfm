enablePlugins(GraalVMNativeImagePlugin)

name := "lfm"

scalaVersion := "3.7.0"

val circeVersion = "0.14.13"
val http4sVersion = "0.23.30"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.http4s" %% "http4s-blaze-client" % "0.23.17",
  "org.http4s" %% "http4s-circe" % http4sVersion,
)

graalVMNativeImageOptions ++= Seq(
  "--enable-https",
  "--initialize-at-build-time",
  "--diagnostics-mode",
  "-H:+ReportExceptionStackTraces",
  "--no-fallback"
)

// trapExit := false
