/*
 * Created by Jacob Summers on 2023.3.30
 * Copyright © 2023 Jacob Summers. All rights reserved.
 * Inspired by https://stackoverflow.com/questions/24771823/spark-streaming-accumulated-word-count
 * and https://github.com/apache/spark/blob/master/examples/src/main/scala/org/apache/spark/examples/streaming/StatefulNetworkWordCount.scala
 */

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, State, StateSpec, StreamingContext}

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

    // Create the context with a 1 second batch size
    val ssc = new StreamingContext(sparkConf, Seconds(10))
    ssc.checkpoint("checkpoint")

    //hardcoded host and port that can be passed in through main method args
    val lines = ssc.socketTextStream("localhost", 9999)
    val words = lines.flatMap(_.split(" "))
    val wordDstream = words.map(x => (x, 1))

    //arbitrary RDD that needs to provide an outline of what the state of data
    //will look like in order to initiate the mapWithState operation
    val initialRDD = ssc.sparkContext.parallelize(List(("hello", 1), ("world", 1)))

    val mappingFunc = (word: String, one: Option[Int], state: State[Int]) => {
      val sum = one.getOrElse(0) + state.getOption.getOrElse(0)
      val output = (word, sum)
      state.update(sum)
      output
    }

    // Update the cumulative count using mapWithState
    // This will give a DStream made of state (which is the cumulative count of the words)
    val stateDstream = wordDstream.mapWithState(
      StateSpec.function(mappingFunc).initialState(initialRDD))

    stateDstream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}
