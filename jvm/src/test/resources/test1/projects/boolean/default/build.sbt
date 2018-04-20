name := "lambdas"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += Resolver.mavenLocal


libraryDependencies += "pl.setblack" % "badlam" % "1.0-SNAPSHOT"

mainClass in (Compile, run) := Some("pl.setblack.slambd.Main")