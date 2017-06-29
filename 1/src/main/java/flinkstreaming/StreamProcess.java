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
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * Created by wilhelmus on 18/06/17.
 */
public class StreamProcess implements Callable {

    private DataStream<String> source;
    private int configIndex;
    private static TwitterSource twitterSource;
    private static BitCoinStream bitCoinStream;
    private static ArrayList<XMLConfiguration> configs;
    private static StreamExecutionEnvironment env;


    public StreamProcess(StreamExecutionEnvironment _env, ArrayList<XMLConfiguration> _configs, int index)
    {
        env = _env;
        configs = _configs;
        configIndex = index;
    }

    private KeySelector keySelect = new KeySelector<Instance, String>() {
        public String getKey(Instance inst) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//            switch(configs.get(configIndex).getString("keyBy.type")){
//                case "static":
//                    return configs.get(configIndex).getString("keyBy.value");
//                case "object":
//                    return String.valueOf(inst.getClass().getDeclaredMethod(configs.get(configIndex).getString("keyBy.function")).invoke(inst));
//                default:
//                    return "1";
//            }
            return "1";
        }
    };

    @Override
    public Object call() throws Exception {
        switch(configs.get(configIndex).getString("source.name")){
            case "twitter":{
                //set Twitter properties to authenticate
                if(twitterSource==null)
                    twitterSource = new TwitterSource(configs.get(configIndex).getString("source.properties-file"));
                // get input data
                source = env.addSource(twitterSource);
            }
            break;
            case "socket":{
                source = env.socketTextStream(configs.get(configIndex).getString("source.ip"),configs.get(configIndex).getInt("source.port"),"\n", 0);
            }
            break;
            case "bitcoin":{
                if(bitCoinStream==null) {
                    Properties prop = new Properties();
                    FileInputStream input = new FileInputStream(configs.get(configIndex).getString("source.properties-file"));
                    prop.load(input);
                    bitCoinStream = new BitCoinStream(prop);
                }
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
                source.flatMap(new StreamParser(configs,configIndex)).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Instance>() {
                    @Override
                    public long extractAscendingTimestamp(Instance element) {
                        return element.getTime();
                    }
                });

        DataStream<String> windowedStream;
        Long windowTime = 0L;
        Long overlapTime = 0L;
        windowTime = windowTime + (configs.get(configIndex).getInt("window.size.hours")*3600) + (configs.get(configIndex).getInt("window.size.minutes")*60) + (configs.get(configIndex).getInt("window.size.seconds"));
        overlapTime = overlapTime + (configs.get(configIndex).getInt("window.overlap.hours")*3600) + (configs.get(configIndex).getInt("window.overlap.minutes")*60) + (configs.get(configIndex).getInt("window.overlap.seconds"));
        switch(configs.get(configIndex).getString("window.type")){
            case "tumbling": {
                switch (configs.get(configIndex).getString("window.time")) {
                    case "event": {
                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(TumblingEventTimeWindows.of(Time.seconds(windowTime)))
                                .apply(new WindowStreamProcess(configs,configIndex));
                        break;
                    }
                    default: {
                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(TumblingProcessingTimeWindows.of(Time.seconds(windowTime)))
                                .apply(new WindowStreamProcess(configs,configIndex));
                        break;
                    }
                }
                break;
            }
            case "sliding":{
                switch (configs.get(configIndex).getString("window.time")) {
                    case "event": {
                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(SlidingEventTimeWindows.of(Time.seconds(windowTime), Time.seconds(overlapTime)))
                                .apply(new WindowStreamProcess(configs,configIndex));
                        break;
                    }
                    default: {
                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(SlidingProcessingTimeWindows.of(Time.seconds(windowTime), Time.seconds(overlapTime)))
                                .apply(new WindowStreamProcess(configs,configIndex));
                        break;
                    }
                }
                break;
            }
            default:{
                switch (configs.get(configIndex).getString("window.time")) {
                    case "event": {
                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(EventTimeSessionWindows.withGap(Time.seconds(windowTime)))
                                .apply(new WindowStreamProcess(configs,configIndex));
                        break;
                    }
                    default: {
                        windowedStream = streamOutput.keyBy(keySelect)
                                .window(ProcessingTimeSessionWindows.withGap(Time.seconds(windowTime)))
                                .apply(new WindowStreamProcess(configs,configIndex));
                        break;
                    }
                }
                break;
            }
        }

//        windowedStream.print();

        switch(configs.get(configIndex).getString("windowSink.type")){
            case "text": {
                windowedStream.writeAsText(configs.get(configIndex).getString("windowSink.path")).setParallelism(1);
                break;
            }
            case "csv": {
                windowedStream.writeAsCsv(configs.get(configIndex).getString("windowSink.path")).setParallelism(1);
                break;
            }
            case "socket":{
                windowedStream.writeToSocket(configs.get(configIndex).getString("windowSink.ip"), configs.get(configIndex).getInt("windowSink.port"), new SerializationSchema<String>() {
                    @Override
                    public byte[] serialize(String s) {
                        return s.getBytes();
                    }
                });
                break;
            }
            case "kafka":{
                FlinkKafkaProducer09<String> myProducer = new FlinkKafkaProducer09<String>(
                        configs.get(configIndex).getString("windowSink.ipPort"),            // broker list
                        configs.get(configIndex).getString("windowSink.topic"),             // target topic
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
        return null;
    }
}
