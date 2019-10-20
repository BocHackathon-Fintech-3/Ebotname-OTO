import com.typesafe.sbt.{GitBranchPrompt, GitVersioning}
import com.typesafe.sbt.SbtNativePackager.autoImport.maintainer

import scala.sys.process.Process

lazy val projectDependencies = Seq(
  ws,
  guice,
  caffeine,
  "com.google.cloud" % "google-cloud-dialogflow" % "0.108.0-alpha",
  "org.joda" % "joda-convert" % "2.2.1",
  "io.lemonlabs" %% "scala-uri" % "1.5.1",
  "net.codingwell" %% "scala-guice" % "4.2.6",
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0-M5" % Test,
  specs2 % Test
)

def evictionSettings: Seq[Setting[_]] = Seq(
  evictionWarningOptions in update := EvictionWarningOptions.default
    .withWarnTransitiveEvictions(false)
    .withWarnDirectEvictions(false)
)

def common: Seq[Setting[_]] = evictionSettings ++ Seq(
  organization := "io.oto",
  version := "0.1.0",
  scalaVersion := "2.12.10",
  licenses := Seq(
    ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))
  ),
  homepage := Some(url("https://oto.io")),
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/",
  // No Documentation on sbt:dist
  sources in (Compile, doc) := Seq.empty,
  publishArtifact in (Compile, packageDoc) := false,
  resolvers += Resolver.url(
    "typesafe",
    url("http://repo.typesafe.com/typesafe/ivy-releases/")
  )(Resolver.ivyStylePatterns),
  resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  resolvers += Resolver.JCenterRepository,
  TaskKey[Unit]("check") := {
    val process =
      Process("java", Seq("-jar", (crossTarget.value / "oto.jar").toString))
    val out = (process !!)
    if (out.trim != "bye") sys.error("unexpected output: " + out)
    ()
  },
  scalacOptions in (Compile, doc) ++= (scalaBinaryVersion.value match {
    case "2.12" => Seq("-no-java-comments")
    case _      => Seq.empty
  }),
  // Setting javac options in common allows IntelliJ IDEA to import them automatically
  javacOptions in compile ++= Seq(
    "-encoding",
    "UTF-8",
    "-source",
    "1.8",
    "-target",
    "1.8",
    "-parameters",
    "-Xlint:unchecked",
    "-Xlint:deprecation"
  )
)

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "io.oto.binders._"

lazy val root = (project in file("."))
  .settings(
    name := "oto",
    maintainer := "hello@ottobot.org",
    libraryDependencies ++= projectDependencies,
    dependencyOverrides ++= Seq()
  )
  .settings(common)
  .enablePlugins(
    PlayScala,
    JavaServerAppPackaging,
    DockerPlugin,
    GitVersioning,
    AshScriptPlugin,
    GitBranchPrompt
  )
  .configure(ApplicationVersionSettings.get)
  .configure(DockerConfigSettings.get)
