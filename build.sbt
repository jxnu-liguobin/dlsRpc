import Dependencies.Versions

//工程通用配置
lazy val commonSettings = Seq(
  organization := "io.github.jxnu-liguobin",
  version := "1.1.2",
  scalaVersion := Versions.scala212,
  Dependencies.commons,
) ++ publishSettings

//publishM2发布到本地maven仓库，在使用pom坐标引入
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
  .settings(commonSettings, Dependencies.common)

javacOptions ++= Seq("-encoding", "UTF-8")
javaOptions in run += "-Xmx1G"

//发布到中央仓库的配置
lazy val publishSettings = Seq(

  useGpg := false,
  pgpPublicRing := new File("/Users/liguobin/.sbt/gpg/pubring.asc"),
  pgpSecretRing := new File("/Users/liguobin/.sbt/gpg/secring.asc"),
  pgpPassphrase := sys.env.get("PGP_PASS").map(_.toArray),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  sonatypeProfileName := organization.value,
  credentials += Credentials(
    "Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    sys.env.getOrElse("SONATYPE_USER", ""),
    sys.env.getOrElse("SONATYPE_PASS", "")
  ),

  isSnapshot := version.value endsWith "SNAPSHOT",
  homepage := Some(url("https://github.com/jxnu-liguobin")),

  scmInfo := Some(
    ScmInfo(
      url("https://github.com/jxnu-liguobin/dlsrpc"),
      "scm:git@github.com:jxnu-liguobin/dlsrpc.git"
    ))
)

lazy val noPublishing = Seq(
  publishTo := None
)

/**
 * 编译路径
 * windows不能使用git cmd 命令行打包，需要使用sbt
 * 发布到本地maven仓库的时候，允许覆盖jar。
 * 发布到仓库后本地maven才能引入，而不再需要加入lib文件
 */