package Streamprocess;

import IndonesianNLP.IndonesianSentenceFormalization;
import IndonesianNLP.IndonesianStemmer;
import Model.Instance;
import Preprocess.FilterIndoTweet;
import Preprocess.NormalizeSentence;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by wilhelmus on 17/05/17.
 */
public class ClusterText implements FlatMapFunction<String, Instance> {

    private transient ObjectMapper jsonParser;
    @Override
    public void flatMap(String value, Collector<Instance> out) throws IOException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }

        JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
        FilterIndoTweet filterIndoTweet = new FilterIndoTweet();
        if(filterIndoTweet.isIndonesian(value)){
            String sentence = jsonNode.get("text").getTextValue().toLowerCase();
            NormalizeSentence normalizer = new NormalizeSentence(sentence);
            out.collect(new Instance(jsonNode.get("id").toString(),normalizer.getSentence(),jsonNode.get("text").getTextValue()));
        }
    }

    
}
