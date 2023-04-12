package provenance.util


import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}

import scala.reflect.ClassTag

case class ProvenanceReceiverInputDStream[T](inputDStream: ReceiverInputDStream[T], provenance: String)
case class ProvenanceDStream[T](dStream: DStream[T], provenance: String)

case class SplitOperation(provenance: String, splitFunc: String => TraversableOnce[String]) {
  def apply(input: ProvenanceReceiverInputDStream[String]): ProvenanceDStream[String] = {
    val outputDStream = input.inputDStream.flatMap(splitFunc)
    ProvenanceDStream(outputDStream, s"${input.provenance} -> Split: '$provenance'")
  }
}

case class MapOperation[T, U : ClassTag](provenance: String, mapFunc: T => U) {
  def apply(input: ProvenanceDStream[T]): ProvenanceDStream[U] = {
    val outputDStream = input.dStream.map(mapFunc)
    ProvenanceDStream(outputDStream, s"${input.provenance} -> Map: '$provenance'")
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
    }
  }

}
