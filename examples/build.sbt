scalaVersion := "2.12.6"
organization := "com.github.radium226"
name := "examples"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

Project.inConfig(Test)(baseAssemblySettings)
Test / assembly / assemblyJarName := s"${name.value}-tests.jar"

libraryDependencies ++= Dependencies.all

dependencyUpdatesFilter -= moduleFilter(organization = "org.apache.kafka")

resolvers += "Confluent" at "http://packages.confluent.io/maven/"
resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

Compile / sourceGenerators  += (avroScalaGenerateSpecific in Compile).taskValue

ThisBuild / scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

Test / assembly / assemblyMergeStrategy := {
  case PathList(paths @ _*) if paths.last endsWith ".properties" =>
    MergeStrategy.concat
  case PathList(paths @ _*) if paths.last endsWith ".conf" =>
    MergeStrategy.concat
  case pathList =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(pathList)
}
