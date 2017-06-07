package Streamprocess;

import Model.Instance;
import Preprocess.TweetFilter;
import Preprocess.NormalizeSentence;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by wilhelmus on 17/05/17.
 */
public class StreamParser implements FlatMapFunction<String, Instance> {

    private transient ObjectMapper jsonParser;
    @Override
    public void flatMap(String value, Collector<Instance> out) throws IOException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }

        JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
        TweetFilter filterIndoTweet = new TweetFilter();
        if(filterIndoTweet.isIndonesian(value)){
            String sentence = jsonNode.get("text").getTextValue().toLowerCase();
            NormalizeSentence normalizer = new NormalizeSentence(sentence);
            out.collect(new Instance(jsonNode.get("id").toString(),normalizer.getSentence(),jsonNode.get("text").getTextValue()));
        }
    }

    
}
