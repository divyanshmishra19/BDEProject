ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.11.8"

ThisBuild / libraryDependencies += "org.apache.spark" %% "spark-core" % "2.2.0"

ThisBuild / libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.2.3"

ThisBuild / libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.2.3"

ThisBuild / libraryDependencies += "org.roaringbitmap" % "RoaringBitmap" % "0.8.11"

ThisBuild /  libraryDependencies += "org.scalatest" %% "scalatest" % "2.11.8" % Test

ThisBuild / libraryDependencies += "com.holdenkarau" %% "spark-testing-base" % "2.11.8" % "test"

ThisBuild / libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.2.0",
  "org.apache.spark" %% "spark-sql" % "3.2.0",
  "org.apache.spark" %% "spark-streaming" % "3.2.0",
  "org.scalatest" %% "scalatest" % "3.2.10" % "test"
)
lazy val root = (project in file("."))
  .settings(
    name := "BDEProject"
  )
