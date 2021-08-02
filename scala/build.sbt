enablePlugins(GraalVMNativeImagePlugin)

name := "lfm"

scalaVersion := "3.0.1"

val circeVersion = "0.14.1"
val http4sVersion = "0.23.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
)

graalVMNativeImageOptions ++= Seq(
  "--enable-https",
  "--initialize-at-build-time",
  "-H:+ReportExceptionStackTraces",
  "--no-fallback"
)

// trapExit := false
