ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.11.8"

ThisBuild / libraryDependencies += "org.apache.spark" %% "spark-core" % "2.2.0"

ThisBuild / libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.3"

ThisBuild / libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.2.3"

ThisBuild / libraryDependencies += "org.roaringbitmap" % "RoaringBitmap" % "0.8.11"

ThisBuild /  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test

lazy val root = (project in file("."))
  .settings(
    name := "BDEProject"
  )
