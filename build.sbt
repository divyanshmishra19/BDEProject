ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / libraryDependencies += "org.apache.spark" %% "spark-core" % "3.3.2"

lazy val root = (project in file("."))
  .settings(
    name := "BDEProject"
  )
