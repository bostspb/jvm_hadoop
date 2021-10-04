import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import scala.Tuple2;

import java.util.Arrays;

public class WordCount {
    public static void main(String[] args) {
        final String input = args[0];
        final String output = args[1];

        final String delimiter = args.length > 2 ? args[3] : " ";

        JavaSparkContext sc = new JavaSparkContext();

        Broadcast<String> broadcasted = sc.broadcast(delimiter);

        sc.textFile(input)
                .flatMap(line -> Arrays.asList(line.split(broadcasted.getValue())).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey(Integer::sum)
                .map(tuple -> tuple._1 + "\t" + tuple._2)
                .saveAsTextFile(output);

        sc.stop();
    }
}
