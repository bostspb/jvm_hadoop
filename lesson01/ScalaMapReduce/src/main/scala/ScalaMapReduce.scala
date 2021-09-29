import org.apache.hadoop.conf.{Configuration, Configured}
import org.apache.hadoop.util.{Tool, ToolRunner}
import org.apache.hadoop.io.{IntWritable, LongWritable, Text}
import org.apache.hadoop.mapreduce.{Job, Mapper, Reducer, Partitioner}
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import scala.jdk.CollectionConverters


object ScalaMapReduce extends Configured with Tool {
  val IN_PATH_PARAM = "swap.input"
  val OUT_PATH_PARAM = "swap.output"

  override def run(args: Array[String]): Int = {
    val job = Job.getInstance(getConf, "Word Count")
    job.setJarByClass(getClass)
    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[IntWritable])
    job.setMapperClass(classOf[TokenizerMapper])
    job.setReducerClass(classOf[IntSumReducer])
    job.setPartitionerClass(classOf[AlphabetPartitioner])
    job.setNumReduceTasks(2)
    val in = new Path(getConf.get(IN_PATH_PARAM))
    val out = new Path(getConf.get(OUT_PATH_PARAM))
    FileInputFormat.addInputPath(job, in)
    FileOutputFormat.setOutputPath(job, out)
    val fs = FileSystem.get(getConf)
    if (fs.exists(out)) fs.delete(out, true)
    if (job.waitForCompletion(true)) 0 else 1
  }

  def main(args: Array[String]): Unit = {
    val res: Int = ToolRunner.run(new Configuration(), this, args)
    System.exit(res)
  }
}

class IntSumReducer extends Reducer[Text, IntWritable, Text, IntWritable] {
  val result = new IntWritable

  //override
  def reduce(key: Text, values: Iterable[IntWritable], context: Reducer[Text, IntWritable, Text, IntWritable]#Context): Unit = {
    var sum = 0
    for (value <- values) {
      sum += value.get
    }
    result.set(sum)
    context.write(key, result)
  }
}

class AlphabetPartitioner extends Partitioner[Text, IntWritable] {
  override def getPartition(key: Text, value: IntWritable, numPartitions: Int): Int = {
    if (numPartitions == 1) return 0
    if (key.getLength > 2) 1
    else 0
  }
}

class TokenizerMapper extends Mapper[Object, Text, Text, IntWritable] {
  val one = new IntWritable(1)
  val text = new Text

  override def map(key: Object, value: Text, context: Mapper[Object, Text, Text, IntWritable]#Context): Unit = {
    val line = value.toString.toLowerCase.replaceAll("[\\\",\\.\\!\\?()\\:#\\/]|\\s{2,}", "")
    val words = line.split("\\s")
    for (word <- words) {
      text.set(word)
      context.write(text, one)
    }
  }
}