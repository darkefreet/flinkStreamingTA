package Kafka;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import java.util.Properties;

/**
 * Created by wilhelmus on 18/06/17.
 */
public class KafkaConsumer {

    public static void main(String[] args) throws Exception {
        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "localhost:9092");
        DataStream<String> stream = env.addSource(new FlinkKafkaConsumer09<String>("clustering-text", new SimpleStringSchema(), properties));

        stream.print();

        env.execute();
    }

}
