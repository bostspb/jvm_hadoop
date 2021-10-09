import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._;

object WordCount {
  def main(args: Array[String]): Unit = {
    val input = args(0)
    val output = args(1)
    val spark = SparkSession.builder().getOrCreate()
    spark.conf.set("spark.sql.shuffle.partitions", 1) // для удобства просмотра результатов

    import spark.sqlContext.implicits._

    // * Измените WordCount так, чтобы он удалял знаки препинания и приводил все слова к единому регистру
    // * Добавьте в WordCount возможность через конфигурацию задать список стоп-слов, которые будут отфильтрованы во время работы приложения
    // Измените выход WordCount так, чтобы сортировка была по количеству повторений, а список слов был во втором столбце, а не в первом

    spark.read.option("header", "true").csv(input)
      .select(concat_ws(" ", $"class", $"comment") as "docs")
      .select(split($"docs","\\s") as "words")
      .select(explode($"words") as "word")
      .select(lower($"word") as "word") // приводил все слова к единому регистру
      .select(regexp_replace($"word", "[\\\",\\.\\!\\?()\\:#\\/]|\\s{2,}", "") as "word") // чтобы он удалял знаки препинания
      .groupBy($"word").count()
      .select($"count", $"word") // список слов был во втором столбце, а не в первом
      .sort(desc("count")) // чтобы сортировка была по количеству повторений
      .write.mode("overwrite").csv(output)

    spark.stop()
  }
}