import spectra.SpectraScore
import scala.io.Source

object Main {
  def main(args: Array[String]): Unit = {
    val provenanceData = Map(
      getProvenanceFromFile("/Users/divyanshmishra/IntelliJ Projects/BDEProject/Logs/log_2023-04-30_12-46-01.txt") -> false,
      getProvenanceFromFile("/Users/divyanshmishra/IntelliJ Projects/BDEProject/Logs/log_2023-04-30_12-46-30.txt") -> true
    )

    val spectraScores = SpectraScore.calculateSpectraScore(provenanceData)
    val mostSuspiciousLine = SpectraScore.findMostSuspiciousLine(spectraScores)

    println(s"The most suspicious line is ${mostSuspiciousLine._1} with a suspiciousness score of ${mostSuspiciousLine._2}")
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
