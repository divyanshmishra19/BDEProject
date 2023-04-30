
/*
 * Created by Violet Harris
 * sources: https://www.scalatest.org/user_guide/sharing_fixtures
 * https://spark.apache.org/docs/latest/streaming-programming-guide.html#testing-applications
 * https://www.scalatest.org/user_guide/selecting_a_style
 * https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/lang/Thread.html#run()
 */

import org.apache.log4j.{Level, LogManager}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, State, StateSpec, StreamingContext}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.apache.spark.streaming._
import org.apache.spark._
import org.apache.spark.streaming.dstream._
import provenance.util.ProvenanceReceiverInputDStream

import java.io.PrintWriter
import java.net.ServerSocket
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global



class WordCountTest extends AnyFunSuite with BeforeAndAfterEach {
  //source: https://www.scalatest.org/user_guide/sharing_fixtures
  var ssc: StreamingContext = _
  override def beforeEach(): Unit = {
    val interval = Seconds(10)

    System.setProperty("hadoop.home.dir", "C:\\hadoop")
    val logger = LogManager.getLogger("org.apache.spark")
    logger.setLevel(Level.ERROR)

    val sparkConf = new SparkConf().setAppName("SpectraWordCount")
      .setMaster("local[*]")
      .set("spark.executor.memory", "512m")
      .set("spark.driver.allowMultipleContexts", "true")

    ssc = new StreamingContext(sparkConf, interval)
    ssc.checkpoint("checkpoint")
    super.beforeEach()
  }

  override def afterEach(): Unit = {
    ssc.stop(stopSparkContext = true, stopGracefully = true)
    super.afterEach()
  }


  test("WordCount.scala") {

    val arrayOfStrings: Array[String]  = Array("hello hi super greeting hello")

    val arrayOfRDDs: Array[RDD[String]] = arrayOfStrings.map(str => ssc.sparkContext.parallelize[String](Seq(str)))
    val rddQueue: mutable.Queue[RDD[String]] = mutable.Queue(arrayOfRDDs: _*)
    val inputDStream: DStream[String] = ssc.queueStream(rddQueue)
    val provenance = "Source: Array of Strings"
    val provenanceInputDStream = ProvenanceReceiverInputDStream(inputDStream, provenance)

    val output: ArrayBuffer[Array[(String, Int)]] = ArrayBuffer()

    val wordCounts = WordCount.countWords(provenanceInputDStream)

    wordCounts.foreachRDD { rdd =>
      output += rdd.collect()
    }

    Future {
      arrayOfRDDs.foreach { rdd =>
        rddQueue.enqueue(rdd)
        Thread.sleep(1000)
      }
    }

    ssc.start()
    ssc.awaitTerminationOrTimeout(10000)
    val answer = output.flatten.groupBy(_._1).mapValues(_.map(_._2).sum)
    val expected = Map("hello" -> 2, "hi" -> 1, "super" -> 1, "greetings" -> 1)
    assert(answer == expected)
  }
}
