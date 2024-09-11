enablePlugins(GraalVMNativeImagePlugin)

name := "lfm"

scalaVersion := "3.5.0"

val circeVersion = "0.14.10"
val http4sVersion = "0.23.28"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.http4s" %% "http4s-blaze-client" % "0.23.16",
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
