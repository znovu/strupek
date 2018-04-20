name := "Strupek/nakolanie"

scalaVersion in ThisBuild := "2.12.5"

lazy val root = project.in(file(".")).
  aggregate(fooJS, fooJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val foo = crossProject.in(file(".")).
  settings(
    name := "foo",
    version := "0.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "0.6.5",
      "org.scalaz" %% "scalaz-core" % "7.2.21",
      "org.scalatest" %%% "scalatest" % "3.0.5" % "test"

    )
  ).
  jvmSettings(
    // Add JVM-specific settings here
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"   % "10.1.1",
      "com.typesafe.akka" %% "akka-stream" % "2.5.12",
      "com.typesafe.akka" %% "akka-http-testkit" % "10.1.1" % "test",
      "org.typelevel" %% "scalaz-scalatest" % "1.1.1" % "test")
  ).
  jsSettings(
    // Add JS-specific settings here
  )

lazy val fooJVM = foo.jvm
lazy val fooJS = foo.js
