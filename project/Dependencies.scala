import sbt.Keys.libraryDependencies
import sbt._

object Dependencies {

  object Versions {
    val scala212 = "2.12.7"
    val log4j = "2.11.1"
    val guava = "19.0"
    val guice = "3.0"
    val netty = "4.1.6.Final"
    val logging = "3.9.2"
    val slfj = "2.1.2"
    val protostuff = "1.0.12"
    val consul = "1.4.2"
    val log4j_api = "11.0"
    val cglib = "3.2.10"
    val config = "1.3.4"
    //    val jmh = "1.9.1"
  }

  object Compiles {


    lazy val config: ModuleID = "com.typesafe" % "config" % Versions.config

    lazy val cglib: ModuleID = "cglib" % "cglib-nodep" % Versions.cglib

    lazy val consulAPi: ModuleID = "com.ecwid.consul" % "consul-api" % Versions.consul

    lazy val guava: ModuleID = "com.google.guava" % "guava" % Versions.guava

    lazy val guice: ModuleID = "com.google.inject" % "guice" % Versions.guice
    //sbt中jmh太难用了，各种问题，试了三个插件都跑步起来
    //    lazy val jmh: Seq[ModuleID] = Seq(
    //      "org.openjdk.jmh" % "jmh-core" % Versions.jmh,
    //      "org.openjdk.jmh" % "jmh-generator-annprocess" % Versions.jmh
    //    )
    lazy val log4j2: Seq[ModuleID] = Seq(
      "org.apache.logging.log4j" %% "log4j-api-scala" % Versions.log4j_api,
      "org.apache.logging.log4j" % "log4j-api" % Versions.log4j,
      "org.apache.logging.log4j" % "log4j-core" % Versions.log4j,
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % Versions.log4j)

    lazy val protostuff: Seq[ModuleID] = Seq(
      "com.dyuproject.protostuff" % "protostuff-core" % Versions.protostuff,
      "com.dyuproject.protostuff" % "protostuff-runtime" % Versions.protostuff)

    lazy val netty: Seq[ModuleID] = Seq(
      "io.netty" % "netty-codec-http2" % Versions.netty,
      "io.netty" % "netty-handler" % Versions.netty)

    lazy val log: ModuleID =
      "com.typesafe.scala-logging" %% "scala-logging" % Versions.logging
  }

  import Compiles._

  val core = libraryDependencies ++= log4j2 ++ protostuff ++ netty ++ Seq(guice, cglib)

  val consuls = libraryDependencies ++= Seq(consulAPi)

  val commos = libraryDependencies ++= Seq(log, config, guava)

}
