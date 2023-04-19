package provenance.util


import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream, PairDStreamFunctions}

import scala.reflect.ClassTag

case class ProvenanceReceiverInputDStream[T](inputDStream: ReceiverInputDStream[T], provenance: String)
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

      println(s"Provenance: ${provenanceDStream.provenance}")
      println(s"Timestamp: $timestamp")
      println(s"Input Data (first 10 elements): $inputData")
      println(s"Output Data (first 10 elements): $outputData")
      println()
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