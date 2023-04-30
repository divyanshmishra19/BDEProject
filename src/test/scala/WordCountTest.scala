
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
import org.scalatest.Assertions._
import org.scalatest.funsuite.AnyFunSuite
import java.io.PrintWriter
import java.net.ServerSocket
import scala.collection.mutable


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


  test("S Word Error") {

    val arrayOfStrings: Array[String]  = Array("hello hi super greetings hello")

    val arrayOfRDDs: Array[RDD[String]] = arrayOfStrings.map(str => ssc.sparkContext.parallelize[String](Seq(str)))
    val rddQueue: mutable.Queue[RDD[String]] = mutable.Queue(arrayOfRDDs: _*)
    val inputDStream: DStream[String] = ssc.queueStream(rddQueue)
    val provenance = "Source: Array of Strings"
    val provenanceInputDStream = ProvenanceReceiverInputDStream(inputDStream, provenance)
    val resultDStream = WordCount.countWords(provenanceInputDStream)
    var actualOutput = Seq.empty[(String, Int)]

    def collectOutput(rdd: RDD[(String, Int)]): Unit = {
      rdd.collect().foreach { case (word, count) =>
        val index = actualOutput.indexWhere(_._1 == word)
        if (index >= 0) {
          val (existingWord, existingCount) = actualOutput(index)
          actualOutput = actualOutput.updated(index, (existingWord, existingCount + count))
        } else {
          actualOutput = actualOutput :+ (word, count)
        }
      }
    }
    resultDStream.foreachRDD(rdd => collectOutput(rdd))

    ssc.start()
    val timeoutMillis = 5000L
    ssc.awaitTerminationOrTimeout(timeoutMillis)
    val expectedOutput = Seq(("hello", 2), ("hi", 1), ("super", 1), ("greetings", 1))
    print("\nExpected Size: " +  expectedOutput.size + "\n")
    print("\nActual Size: " +  actualOutput.size + "\n")
    print("\nExpected WordCount Output" +  expectedOutput + "\n")
    print("\nActual WordCount Output" + actualOutput + "\n")

    assert(expectedOutput.toMap == actualOutput.toMap)

  }
  // Define the function to assert that the expected and actual outputs are equal


}
