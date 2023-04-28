import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}

class MyStreamingTests extends org.scalatest.funsuite.AnyFunSuite {

  test("word count test") {
    val newSession = SparkSession.builder()
      .appName("WordCountTest")
      .master("local[*]")
      .getOrCreate()
    val ssc = new StreamingContext(newSession.sparkContext, Seconds(1))

    // creates DStream
    val data_stream = ssc.socketTextStream("localhost", 9999)

    // Counts words per batch
    val counts = data_stream
      .flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_ + _)

    // starts stream
    ssc.start()

    // waiting for a couple seconds to check the results of
    // the word count
    Thread.sleep(3000)

    val expected_count = Seq(("this", 1), ("is", 1), ("a", 1), ("test", 1))
    var actual_count = Seq[(String, Int)]()
    counts.foreachRDD(rdd => actual_count = rdd.collect())
    assert(expected_count === actual_count)

    // stops stream
    ssc.stop(stopSparkContext = true, stopGracefully = true)
  }

}