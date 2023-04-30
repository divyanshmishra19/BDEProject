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
import provenance.util.WordCountOperationLineNumber.getOperationLineNumber
import provenance.util.{FilterOperation, MapOperation, ProvenanceDStream, ProvenanceReceiverInputDStream, SplitOperation, UpdateStateByKeyOperation}
import scala.List

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

    val filterFunc1= (x:String) => x.startsWith("s")
    val filterOp1 = FilterOperation[String]("words that start with s", filterFunc1)

    val filterFunc2 = (x: String) => !x.startsWith("s")
    val filterOp2 = FilterOperation[String]("words that don't start with s", filterFunc2)

    val mapFunc1 = (x: String) => (x, 1000)
    val mapOp1 = MapOperation[String, (String, Int)]("s words to 1000", mapFunc1)

    val mapFunc2 = (x:String) => (x,1)
    val mapOp2 = MapOperation[String, (String, Int)]("all other words to 1", mapFunc2)

    val wordDstream1 = mapOp1(filterOp1(words))
    val wordDstream2 = mapOp2(filterOp2(words))

    val updateFunc = (values: Seq[Int], state: Option[Int]) => {
      val currentCount = values.foldLeft(0)(_ + _)

      val previousCount = state.getOrElse(0)

      Some(currentCount + previousCount)
    }


    val updateStateByKeyOperation = UpdateStateByKeyOperation[String, Int, Int]("word count update", updateFunc)
    val stateDstream1 = updateStateByKeyOperation(wordDstream1)
    val stateDstream2 = updateStateByKeyOperation(wordDstream2)

    // Log provenance information for both operations
    logProvenance(stateDstream1)
    logProvenance(stateDstream2)




    // Update the cumulative count using updateStateByKey
    // This will give a Dstream made of state (which is the cumulative count of the words)
    stateDstream1.dStream.print()
    stateDstream2.dStream.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
