import org.apache.spark.SparkContext

object WordCount {
  def main(args: Array[String]): Unit = {
    val input = args(0)
    val output = args(1)
    val sc = new SparkContext()
    sc.textFile(input)
      .flatMap(line => line.split("\\s"))
      .map(word => (word.toString.toLowerCase.replaceAll("[\\\",\\.\\!\\?()\\:#\\/]|\\s{2,}", ""), 1))
      .reduceByKey(_ + _) // .reduceByKey((x, y) => x + y)
      .sortBy { case (word, count) => count } // .sortBy(_._2) or .sortBy(pair => pair._2)
      .map(tuple => tuple._2 + "; \"" + tuple._1 + "\"")
      .saveAsTextFile(output)
    sc.stop()
  }
}