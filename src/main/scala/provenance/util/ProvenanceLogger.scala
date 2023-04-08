package provenance.util

import com.google.gson.Gson

case class ProvenanceData[T, U](
                                 inputData: T,
                                 outputData: U,
                                 timestamp: Long,
                                 operation: String
                               )

object ProvenanceLogger {

  def withProvenance[T, U](operation: String)(func: T => U): T => U = { input =>
    val output = func(input)
    val currentTime = System.currentTimeMillis()
    val provenance = ProvenanceData(input.toString, output.toString, currentTime, operation)
    logProvenanceData(provenance)

    output
  }

  def logProvenanceData[T, U](provenance: ProvenanceData[T, U]): Unit = {
    val gson = new Gson()
    val jsonString = gson.toJson(provenance)
    println(jsonString)
  }

  /*
  def withProvenance[I, O](operation: String)(func: DStream[I] => DStream[O]): DStream[I] => DStream[O] = { input =>
    val output = func(input)

    // Process each RDD within the input and output DStreams
    input.foreachRDD { rdd =>
      val startTime = System.currentTimeMillis()
      val outputRDD = output.asInstanceOf[DStream[O]].ssc.sparkContext.union(output.generatedRDDs.values.toSeq)
      val endTime = System.currentTimeMillis()

      // Extract relevant data from the input and output RDDs
      val inputProvenanceData = ProvenanceStreamData(rdd.collect().toList, rdd.count())
      val outputProvenanceData = ProvenanceStreamData(outputRDD.collect().toList, outputRDD.count())

      val provenance = ProvenanceData(inputProvenanceData, outputProvenanceData, endTime, operation)
      // Log the provenance data
      logProvenanceData(provenance)
    }

    output
  } */

}