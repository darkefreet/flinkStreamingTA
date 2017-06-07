package Streamprocess;

import Model.Instance;
import Preprocess.TweetFilter;
import Preprocess.NormalizeSentence;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by wilhelmus on 17/05/17.
 */
public class StreamParser implements FlatMapFunction<String, Instance> {

    private transient XMLConfiguration config;
    private transient ObjectMapper jsonParser;

    public StreamParser() throws ConfigurationException {
        config = new XMLConfiguration("config.xml");
    }

    @Override
    public void flatMap(String value, Collector<Instance> out) throws IOException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }

        switch(config.getString("data.type")){
            case "tweet":{
                JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
                TweetFilter tweetFilter = new TweetFilter(config);
                if(tweetFilter.filter(value)){
                    String sentence = jsonNode.get("text").getTextValue().toLowerCase();
                    NormalizeSentence normalizer = new NormalizeSentence(sentence);
                    out.collect(new Instance(jsonNode.get("id").toString(),normalizer.getSentence(),jsonNode));
                }
                break;
            }
            default:{
                JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
                TweetFilter tweetFilter = new TweetFilter(config);
                if(tweetFilter.filter(value)){
                    String sentence = jsonNode.get("text").getTextValue().toLowerCase();
                    NormalizeSentence normalizer = new NormalizeSentence(sentence);
                    out.collect(new Instance(jsonNode.get("id").toString(),normalizer.getSentence(),jsonNode));
                }
            }
        }

    }

}
