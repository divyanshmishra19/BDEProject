import spectra.SpectraScore

object Main {
  def main(args: Array[String]): Unit = {
    val provenanceData = Map(
      "Source: SocketTextStream -> Split: ' ' (39) -> Map: 'x => (x, 1000)' (49) -> UpdateStateByKey: 'word count update' (61)" -> false,
      "Source: SocketTextStream -> Split: ' ' (39) -> Map: 'x => (x, 1)' (51) -> UpdateStateByKey: 'word count update' (61)" -> true
    )

    val spectraScores = SpectraScore.calculateSpectraScore(provenanceData)
    val mostSuspiciousLine = SpectraScore.findMostSuspiciousLine(spectraScores)

    println(s"The most suspicious line is ${mostSuspiciousLine._1} with a suspiciousness score of ${mostSuspiciousLine._2}")
  }
}
