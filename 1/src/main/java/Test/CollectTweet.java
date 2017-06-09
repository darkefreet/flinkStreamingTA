package Test;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.twitter.TwitterSource;
import org.apache.flink.util.Collector;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by wilhelmus on 09/06/17.
 */
public class CollectTweet {

    private static DataStream<String> source;
    private static transient ObjectMapper jsonParser;
    public static void main(String[] args) throws Exception {

        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();

        TwitterSource twitterSource = new TwitterSource("twitter.properties");
        // get input data
        source = env.addSource(twitterSource);

        DataStream<String> streamOutput =
                source.flatMap(new FlatMapFunction<String, String>() {
                    @Override
                    public void flatMap(String s, Collector<String> collector) throws Exception {
                        if(jsonParser ==null){
                            jsonParser = new ObjectMapper();
                        }
                        JsonNode jsonNode = jsonParser.readValue(s,JsonNode.class);
                        if(jsonNode.has("lang")){
                            if(jsonNode.get("lang").getTextValue().equals("in"))
                                collector.collect(s);
                        }
                    }
                });

        streamOutput.writeAsText("twitter-indo.txt").setParallelism(1);

        env.execute();
    }
}

