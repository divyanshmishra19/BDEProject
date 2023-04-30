package provenance.util


import org.apache.spark.streaming.dstream.{DStream, PairDStreamFunctions, ReceiverInputDStream}

import java.io.{File, PrintWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.reflect.ClassTag

case class ProvenanceReceiverInputDStream[T](inputDStream: DStream[T], provenance: String)
case class ProvenanceDStream[T](dStream: DStream[T], provenance: String, lineNumber: Int)

case class SplitOperation(provenance: String, splitFunc: String => TraversableOnce[String]) {
  def apply(input: ProvenanceReceiverInputDStream[String]): ProvenanceDStream[String] = {
    val outputDStream = input.inputDStream.flatMap(splitFunc)
    val lineNumber = WordCountOperationLineNumber.getOperationLineNumber
    val provenanceString = Provenance.createProvenanceString(input.provenance, "Split", provenance, lineNumber)
    ProvenanceDStream(outputDStream, provenanceString, lineNumber)
  }
}

case class MapOperation[T, U : ClassTag](provenance: String, mapFunc: T => U) {
  def apply(input: ProvenanceDStream[T]): ProvenanceDStream[U] = {
    val outputDStream = input.dStream.map(mapFunc)
    val lineNumber = WordCountOperationLineNumber.getOperationLineNumber
    val provenanceString = Provenance.createProvenanceString(input.provenance, "Map", provenance, lineNumber)
    ProvenanceDStream(outputDStream, provenanceString, lineNumber)
  }
}

case class FilterOperation[T](provenance: String, filterFunc: T => Boolean) {
  def apply(input: ProvenanceDStream[T]): ProvenanceDStream[T] = {
    val outputDStream = input.dStream.filter(filterFunc)
    val lineNumber = WordCountOperationLineNumber.getOperationLineNumber
    val provenanceString = Provenance.createProvenanceString(input.provenance, "Filter", provenance, lineNumber)
    ProvenanceDStream(outputDStream, provenanceString, lineNumber)
  }
}

case class UpdateStateByKeyOperation[K: ClassTag, V: ClassTag, S: ClassTag](
                                                                             provenance: String,
                                                                             updateFunc: (Seq[V], Option[S]) => Option[S]
                                                                           ) {
  def apply(input: ProvenanceDStream[(K, V)]): ProvenanceDStream[(K, S)] = {
    val outputDStream = input.dStream.updateStateByKey(updateFunc)
    val lineNumber = WordCountOperationLineNumber.getOperationLineNumber
    val provenanceString = Provenance.createProvenanceString(input.provenance, "UpdateStateByKey", provenance, lineNumber)
    ProvenanceDStream(outputDStream, provenanceString, lineNumber)
  }
}



object Provenance {

  import org.apache.spark.streaming.Time

  def logProvenance[T](provenanceDStream: ProvenanceDStream[T]): Unit = {
    provenanceDStream.dStream.foreachRDD { (rdd, time: Time) =>
      val timestamp = time.toString
      val inputData = rdd.take(10).mkString(", ")
      val outputData = rdd.take(10).mkString(", ")
      val logDirectoryPath = "/Users/divyanshmishra/IntelliJ Projects/BDEProject/Logs"

      val currentDateTime = LocalDateTime.now()
      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
      val logFileName = s"log_${currentDateTime.format(formatter)}.txt"
      val logFilePath = s"$logDirectoryPath/$logFileName"

      val logString =
        s"""Provenance: ${provenanceDStream.provenance}
           |Timestamp: $timestamp
           |Input Data (first 10 elements): $inputData
           |Output Data (first 10 elements): $outputData
           |""".stripMargin

      // Append the log to the specified file
      val pw = new PrintWriter(new File(logFilePath), "UTF-8")
      pw.append(logString + "\n")
      pw.close()

      // Print the log to the console
      println(logString)
    }
  }

  def createProvenanceString(inputProvenance: String, operationName: String, provenance: String, lineNumber: Int): String = {
    s"$inputProvenance -> $operationName: '$provenance' (Line: $lineNumber)"
  }

}

object WordCountOperationLineNumber {
  def getOperationLineNumber: Int = {
    val number = Thread.currentThread.getStackTrace
      .dropWhile {
        s =>
          s.getClassName.startsWith("provenance.util") ||
            s.getClassName.startsWith("java.lang")
      }(0)
      .getLineNumber
    number
  }
}