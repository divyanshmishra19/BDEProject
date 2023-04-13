/*
 * Created by Jacob Summers on 2023.3.30
 * Copyright © 2023 Jacob Summers. All rights reserved.
 * Inspired by https://stackoverflow.com/questions/24771823/spark-streaming-accumulated-word-count
 * and https://github.com/apache/spark/blob/master/examples/src/main/scala/org/apache/spark/examples/streaming/StatefulNetworkWordCount.scala
 */

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import provenance.util.Provenance.logProvenance
import provenance.util.{MapOperation, ProvenanceDStream, ProvenanceReceiverInputDStream, SplitOperation}

object WordCount {
  def main(args: Array[String]): Unit = {

    val logger = LogManager.getLogger("org.apache.spark")
    logger.setLevel(Level.ERROR)

    //comment this out if not on windows
    System.setProperty("hadoop.home.dir", "C:\\hadoop")

    val sparkConf = new SparkConf().setAppName("SpectraWordCount")
      .setMaster("local[*]")
      .set("spark.executor.memory", "512m")
      .set("spark.driver.extraJavaOptions", "-Dlog4j.configuration=none")

    //connection to spark container
    //.setMaster("spark://localhost:7077")
    // Create the context with a 10 second batch size
    val ssc = new StreamingContext(sparkConf, Seconds(10))
    ssc.checkpoint("checkpoint")

    // Create a NetworkInputDStream on target ip:port and count the
    // words in input stream of \n delimited test (eg. generated by 'nc')
    val splitOperation = SplitOperation(" ", _.split(" "))
    val lines = ProvenanceReceiverInputDStream(ssc.socketTextStream("localhost", 9999), "Source: SocketTextStream")
    val words = splitOperation(lines)

    // list.map(x => if (x % 2 == 0) x * 2 else x / 2)

    val mapOperation = MapOperation[String, (String, Int)]("x => if(x.substring(0,1).equalsIgnoreCase(\"s\")) (x, 1000) else (x, 1)", x => if(x.substring(0,1).equalsIgnoreCase("s")) (x, 1000) else (x, 1))
    val wordDstream = mapOperation(words)



    val updateFunc = (values: Seq[Int], state: Option[Int]) => {
      val currentCount = values.foldLeft(0)(_ + _)

      val previousCount = state.getOrElse(0)

      Some(currentCount + previousCount)
    }

    val stateDstream = wordDstream.dStream.updateStateByKey[Int](updateFunc)
    val provenanceStateDstream = ProvenanceDStream(stateDstream, s"${wordDstream.provenance} -> UpdateStateByKey")
    provenanceStateDstream.dStream.print()
    // Log provenance information for both operations
    logProvenance(words)
    logProvenance(wordDstream)
    logProvenance(provenanceStateDstream)


    // Update the cumulative count using updateStateByKey
    // This will give a Dstream made of state (which is the cumulative count of the words)
    //val stateDstream = wordDstream.updateStateByKey[Int](updateFunc)
    stateDstream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
