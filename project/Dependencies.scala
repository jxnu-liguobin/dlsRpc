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
  }

  object Compiles {

    lazy val config: ModuleID = "com.typesafe" % "config" % Versions.config

    lazy val cglib: ModuleID = "cglib" % "cglib-nodep" % Versions.cglib

    lazy val consulAPi: ModuleID = "com.ecwid.consul" % "consul-api" % Versions.consul

    lazy val guava: ModuleID = "com.google.guava" % "guava" % Versions.guava

    lazy val guice: ModuleID = "com.google.inject" % "guice" % Versions.guice

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

  //RPC调用
  val core = libraryDependencies ++= protostuff ++ netty ++ Seq(guice, cglib)

  //服务注册发现
  val consuls = libraryDependencies ++= Seq(consulAPi)

  //配置、工具、常量
  val common = libraryDependencies ++= Seq(config)

  //通用依赖
  val commons = libraryDependencies ++= log4j2 ++ Seq(log, guava)

}
