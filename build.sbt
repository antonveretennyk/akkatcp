import com.trueaccord.scalapb.{ScalaPbPlugin => PB}

PB.protobufSettings

PB.runProtoc in PB.protobufConfig := (args => com.github.os72.protocjar.Protoc.runProtoc("-v261" +: args.toArray))

resolvers in ThisBuild ++= Seq("Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
    Resolver.mavenLocal)

resolvers += "Logback repo" at "https://mvnrepository.com/artifact/ch.qos.logback/logback-classic"

name := "akkatcp"

version := "0.1-SNAPSHOT"

organization := "com.tennik"

scalaVersion in ThisBuild := "2.11.8"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.2",
  "com.typesafe.akka" %% "akka-remote" % "2.4.2",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.2",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.2",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
//  "com.typesafe.slick" %% "slick" % "3.1.1",
//  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
//  "org.slf4j" % "slf4j-nop" % "1.6.4",
//  "org.postgresql" % "postgresql" % "9.4.1208.jre7",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.specs2" %% "specs2-core" % "3.8.7" % "test",
  "org.scalactic" %% "scalactic" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  
)

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)
