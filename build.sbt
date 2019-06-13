import Dependencies.Versions
import sbtassembly.MergeStrategy

//工程通用配置
lazy val commonSettings = Seq(
  organization := "io.growing",
  version := "1.0.3",
  scalaVersion := Versions.scala212
)

//根项目配置，benchmark与本项目无直接依赖关系，所以导入benchmark时需要手动关联dlsRpc
//benchmark现在需要使用package打包，以jar包的形式依赖dlsRpc
lazy val root = Project(id = "dlsRpc", base = file("."))
  .settings(commonSettings).aggregate(consuls, core, commons)

//核心实现，依赖consuls，commons
lazy val core = Project(id = "dlsRpc-core", base = file("dlsRpc-core"))
  .settings(commonSettings, Dependencies.core).dependsOn(commons, consuls)

//服务注册发现，可依赖commons但不能反向依赖core
lazy val consuls = Project(id = "dlsRpc-consul", base = file("dlsRpc-consul"))
  .settings(commonSettings, Dependencies.consuls).dependsOn(commons)

//通用工具和隐式对象，不可反向依赖core，consuls
lazy val commons = Project(id = "dlsRpc-common", base = file("dlsRpc-common"))
  .settings(commonSettings, Dependencies.commos)

javacOptions ++= Seq("-encoding", "UTF-8")
javaOptions in run += "-Xmx1G"

//编译路径
//windows不能使用git cmd 命令行打包，需要使用sbt