package flinkstreaming;

import DataSource.BitCoinStream;
import Model.Instances.Instance;
import Preprocess.DocumentsSVD;
import Streamprocess.StreamParser;
import Streamprocess.WindowStreamProcess;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.*;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer09;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;
import org.apache.flink.streaming.util.serialization.SerializationSchema;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * Created by wilhelmus on 18/06/17.
 */
public class StreamProcess implements Callable {

    private static DataStream<String> source;
    private static XMLConfiguration config;

    public StreamProcess(XMLConfiguration _config){
        config = _config;
    }

    @Override
    public Object call() throws Exception {
        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();

        switch(config.getString("source.name")){
            case "twitter":{
                //set Twitter properties to authenticate
                TwitterSource twitterSource = new TwitterSource(config.getString("source.properties-file"));
                // get input data
                source = env.addSource(twitterSource);
            }
            break;
            case "socket":{
                source = env.socketTextStream(config.getString("source.ip"),config.getInt("source.port"),"\n", 0);
            }
            break;
            case "bitcoin":{
                Properties prop = new Properties();
                FileInputStream input = new FileInputStream(config.getString("source.properties-file"));
                prop.load(input);
                BitCoinStream bitCoinStream = new BitCoinStream(prop);
                source = env.addSource(bitCoinStream);
                break;
            }
            default: {
                System.out.println("No source given");
                source = null;
            }
        }

        //test
        DataStream<Instance> streamOutput =
                source.flatMap(new StreamParser(config)).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Instance>() {
                    @Override
                    public long extractAscendingTimestamp(Instance element) {
                        return element.getTime();
                    }
                });

        KeySelector keySelect = new KeySelector<Instance, String>() {
            public String getKey(Instance inst) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
                switch(config.getString("keyBy.type")){
                    case "static":
                        return config.getString("keyBy.value");
                    case "object":
                        return String.valueOf(inst.getClass().getDeclaredMethod(config.getString("keyBy.function")).invoke(inst));
                    default:
                        return "1";
                }
            }
        };

        DataStream<String> windowedStream;
        Long windowTime = 0L;
        Long overlapTime = 0L;
        windowTime = windowTime + (config.getInt("window.size.hours")*3600) + (config.getInt("window.size.minutes")*60) + (config.getInt("window.size.seconds"));
        overlapTime = overlapTime + (config.getInt("window.overlap.hours")*3600) + (config.getInt("window.overlap.minutes")*60) + (config.getInt("window.overlap.seconds"));

        DocumentsSVD docSVD = new DocumentsSVD();
        if(config.getString("textProcess.anyText").equals("true")){
            docSVD.makeSVDModel(config.getString("textProcess.dataPath"),config.getString("textProcess.label"),config.getString("textProcess.resource"));
        }

        switch(config.getString("window.type")){
            case "tumbling": {
                switch (config.getString("window.time")) {
                    case "event": {
                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(TumblingEventTimeWindows.of(Time.seconds(windowTime)))
                                .apply(new WindowStreamProcess(docSVD,config));
                        break;
                    }
                    default: {

                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(TumblingProcessingTimeWindows.of(Time.seconds(windowTime)))
                                .apply(new WindowStreamProcess(docSVD,config));
                        break;
                    }
                }
                break;
            }
            case "sliding":{
                switch (config.getString("window.time")) {
                    case "event": {
                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(SlidingEventTimeWindows.of(Time.seconds(windowTime), Time.seconds(overlapTime)))
                                .apply(new WindowStreamProcess(docSVD,config));
                        break;
                    }
                    default: {
                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(SlidingProcessingTimeWindows.of(Time.seconds(windowTime), Time.seconds(overlapTime)))
                                .apply(new WindowStreamProcess(docSVD,config));
                        break;
                    }
                }
                break;
            }
            default:{
                switch (config.getString("window.time")) {
                    case "event": {
                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(EventTimeSessionWindows.withGap(Time.seconds(windowTime)))
                                .apply(new WindowStreamProcess(docSVD,config));
                        break;
                    }
                    default: {
                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(ProcessingTimeSessionWindows.withGap(Time.seconds(windowTime)))
                                .apply(new WindowStreamProcess(docSVD,config));
                        break;
                    }
                }
                break;
            }
        }

        switch(config.getString("windowSink.type")){
            case "text": {
                windowedStream.writeAsText(config.getString("windowSink.path")).setParallelism(1);
                break;
            }
            case "csv": {
                windowedStream.writeAsCsv(config.getString("windowSink.path")).setParallelism(1);
                break;
            }
            case "socket":{
                windowedStream.writeToSocket(config.getString("windowSink.ip"), config.getInt("windowSink.port"), new SerializationSchema<String>() {
                    @Override
                    public byte[] serialize(String s) {
                        return s.getBytes();
                    }
                });
                break;
            }
            case "kafka":{
                FlinkKafkaProducer09<String> myProducer = new FlinkKafkaProducer09<String>(
                        config.getString("windowSink.ipPort"),            // broker list
                        config.getString("windowSink.topic"),             // target topic
                        new SimpleStringSchema());   // serialization schema
                // the following is necessary for at-least-once delivery guarantee
                myProducer.setLogFailuresOnly(true);   // "false" by default
                myProducer.setFlushOnCheckpoint(true);  // "false" by default
                windowedStream.addSink(myProducer);
                break;
            }
            default:{
                windowedStream.print();
                break;
            }
        }
        // execute program
        env.execute("Java word count from SocketTextStream Example");

        return null;
    }
}
