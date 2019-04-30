name := "Strupek/nakolanie"

scalaVersion in ThisBuild := "2.12.5"

lazy val root = project.in(file(".")).
  aggregate(strupekJS, strupekJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val strupek = crossProject.in(file(".")).
  settings(
    name := "strupek",
    version := "0.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "0.6.6",
      "org.scalaz" %% "scalaz-core" % "7.2.21",
      "org.scalaz" %% "scalaz-concurrent" % "7.2.21",
      "org.scalatest" %%% "scalatest" % "3.0.5" % "test",
      "org.scala-js" %% "scalajs-library" % "0.6.23",
      "biz.enef" %%% "slogging" % "0.6.1"
    )
  ).
  jvmSettings(
    // Add JVM-specific settings here
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.1.1",
      "com.typesafe.akka" %% "akka-stream" % "2.5.14",
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.14" % Test,
      // https://mvnrepository.com/artifact/commons-io/commons-io
      "commons-io" % "commons-io" % "2.6",
      "com.typesafe.akka" %% "akka-http-testkit" % "10.1.1" % "test",
      "org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
      "org.typelevel" %% "scalaz-scalatest" % "1.1.1" % "test",
      "io.verizon.delorean" %% "core" % "1.2.42-scalaz-7.2"),

      mainClass := Some("WebServer")
  ).
  jsSettings(
    // Add JS-specific settings here
    // https://mvnrepository.com/artifact/org.scala-js/scalajs-dom
      scalacOptions ++= {
        val a = "file:///home/jarek/dev/strupek/nakolanie/shared/"
        val b = "file:///home/jarek/dev/strupek/nakolanie/js/"
        val g = "../../"

        Seq(s"-P:scalajs:mapSourceURI:$a->$g", s"-P:scalajs:mapSourceURI:$b->$g")
      },
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.6",
      scalaJSUseMainModuleInitializer := true
)

lazy val strupekJVM = strupek.jvm
lazy val strupekJS = strupek.js
