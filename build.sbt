import Dependencies.Versions
import sbt.util


name := "dlsRpc"
version := "1.0.0"
scalaVersion := Versions.scala212
lazy val root = Project(id = "dlsRpc", base = file("."))
  .settings(
    organization := "io.growing",
    scalaVersion := Versions.scala212,
  ).settings(Dependencies.core)


logLevel := util.Level.Warn
javacOptions ++= Seq("-encoding", "UTF-8")
javaOptions in run += "-Xmx1G"