//
///*
// * Created by Violet Harris
// * sources: https://www.scalatest.org/user_guide/sharing_fixtures
// * https://spark.apache.org/docs/latest/streaming-programming-guide.html#testing-applications
// * https://www.scalatest.org/user_guide/selecting_a_style
// * https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/lang/Thread.html#run()
// */
//
//import org.apache.spark.SparkConf
//import org.apache.spark.streaming.{Seconds, State, StateSpec, StreamingContext}
//import org.scalatest.BeforeAndAfterEach
//import org.scalatest.funsuite.AnyFunSuite
//
//import java.io.PrintWriter
//import java.net.ServerSocket
//
//import objects.TaintString
//
//class WordCountTest extends AnyFunSuite with BeforeAndAfterEach {
//  val interval = Seconds(5)
//
//  val sparkConf = new SparkConf().setAppName("SpectraWordCount")
//    .setMaster("local[*]")
//    .set("spark.executor.memory", "512m")
//
//  var ssc: StreamingContext = _
//
//  //source: https://www.scalatest.org/user_guide/sharing_fixtures
//  override def beforeEach(): Unit = {
//    ssc = new StreamingContext(sparkConf, interval)
//    super.beforeEach()
//  }
//
//  override def afterEach(): Unit = {
//    ssc.stop(stopSparkContext = true, stopGracefully = true)
//    super.afterEach()
//  }
//
//
//  test("WordCount.scala") {
//
//    val words = Seq("Violet Harris", "this is a test", "count the number of words", "lol")
//
//
//    //lines.flatMap(_.split(" ")).map(word => (word, 1)).reduceByKey(_ + _).print()
//    val server = new Thread {
//      override def run(): Unit = {
//        val lstnr = new ServerSocket(9999)
//        val sckt = lstnr.accept()
//        val out = new PrintWriter(sckt.getOutputStream(), true)
//        words.foreach(data => out.write(data + "\n"))
//        lstnr.close()
//        out.close()
//        sckt.close()
//      }
//    }
//    server.start()
//
//    val lines = ssc.socketTextStream("localhost", 9999)
//
//    val socket = new ServerSocket(9999)
//    val printer = new PrintWriter(socket.accept().getOutputStream(), true)
//
//    WordCount.main(Array.empty[String])
//
//    printer.println("Violet Harris")
//    printer.println("this is a test")
//
//    Thread.sleep(100)
//
//    val output = ssc.sparkContext.parallelize(Seq(("hello", 1), ("world", 1), ("foo", 1), ("bar", 1), ("baz", 1), ("qux", 1)))
//    assert(output.count() === 6)
//  }
//}
