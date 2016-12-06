name := """hello-play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(  
  javaCore,
  javaJdbc,
  cache,
  javaWs,  
  //https://www.playframework.com/documentation/2.5.9/JavaJPA
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "5.2.4.Final",
  //  http://mvnrepository.com/artifact/mysql/mysql-connector-java  
  "mysql" % "mysql-connector-java" % "5.1.36",
  //  http://mvnrepository.com/artifact/org.mindrot/jbcrypt/
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.webjars" %% "webjars-play" % "2.5.0",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.apache.commons" % "commons-email" % "1.4",
  "com.typesafe.play" %% "play-mailer" % "5.0.0"
  )

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
//routesGenerator := InjectedRoutesGenerator
routesGenerator := StaticRoutesGenerator

PlayKeys.externalizeResources := false