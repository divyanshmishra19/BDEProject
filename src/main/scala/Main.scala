import spectra.SpectraScore

import scala.collection.mutable
import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {

    val provenanceData = promptUserForProvenance()
    val spectraScores = SpectraScore.calculateSpectraScore(provenanceData)
    val mostSuspiciousLine = SpectraScore.findMostSuspiciousLine(spectraScores)

    println(s"The most suspicious line is ${mostSuspiciousLine._1} with a suspiciousness score of ${mostSuspiciousLine._2}")
  }

  def promptUserForProvenance(): Map[String, Boolean] = {
    val provenanceData = mutable.Map.empty[String, Boolean]
    var promptUser = true
    while (promptUser) {
      println(s"Enter the name of log file generated from test run.")
      val name: String = scala.io.StdIn.readLine()

      println(s"Did it pass? y for Yes. n for No.")
      val testAnswer: String = scala.io.StdIn.readLine()
      val fail: Boolean = if (testAnswer.equalsIgnoreCase("y")) true else false

      val provenanceString = getProvenanceFromFile(s"./Logs/$name")
      provenanceData += (provenanceString -> fail)

      println(s"Enter c to continue entering log files. Enter x to stop.")
      val enterAnswer: String = scala.io.StdIn.readLine()
      if (enterAnswer.equalsIgnoreCase("x")) promptUser = false
    }
    provenanceData.toMap
  }

  def getProvenanceFromFile(filePath: String): String = {
    val fileContent = Source.fromFile(filePath, "UTF-8").getLines().mkString("\n")
    val provenanceLine = fileContent.split("\n").find(line => line.startsWith("Provenance:"))

    provenanceLine match {
      case Some(line) => line.stripPrefix("Provenance: ").trim
      case None => "Provenance not found in the log file."
    }
  }
}
