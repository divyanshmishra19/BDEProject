package spectra

object SpectraScore {
  def calculateSpectraScore(provenanceData: Map[String, Boolean]): Map[Int, (Int, Int)] = {
    val lineCounters = scala.collection.mutable.Map[Int, (Int, Int)]()

    provenanceData.foreach { case (provenance, isCorrect) =>
      val lineNumberPattern = "\\((\\d+)\\)".r
      val lineNumbers = lineNumberPattern.findAllIn(provenance).map(m => m.substring(1, m.length - 1).toInt).toList

      lineNumbers.foreach { lineNumber =>
        lineCounters.get(lineNumber) match {
          case Some((correct, incorrect)) =>
            if (isCorrect) {
              lineCounters.update(lineNumber, (correct + 1, incorrect))
            } else {
              lineCounters.update(lineNumber, (correct, incorrect + 1))
            }
          case None =>
            if (isCorrect) {
              lineCounters += (lineNumber -> (1, 0))
            } else {
              lineCounters += (lineNumber -> (0, 1))
            }
        }
      }
    }

    lineCounters.toMap
  }
  def findMostSuspiciousLine(spectraScores: Map[Int, (Int, Int)]): (Int, Double) = {
    val totalCorrect = spectraScores.values.map(_._1).sum.toDouble
    val totalIncorrect = spectraScores.values.map(_._2).sum.toDouble

    val suspiciousnessScores = spectraScores.map { case (lineNumber, (correct, incorrect)) =>
      val score = (incorrect / totalIncorrect) / ((incorrect / totalIncorrect) + (correct / totalCorrect))
      (lineNumber, score)
    }

    suspiciousnessScores.maxBy(_._2)
  }
}
