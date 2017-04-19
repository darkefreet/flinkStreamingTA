package Preprocess;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by wilhelmus on 19/04/17.
 */
public class FilterIndo implements FlatMapFunction<String, String> {

    private transient ObjectMapper jsonParser;
    @Override
    public void flatMap(String value, Collector<String> out) throws IOException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }

        JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
        boolean isIndonesian = jsonNode.has("user") && jsonNode.get("user").get("lang").getTextValue().equals("id");
        boolean hasText = jsonNode.has("created_at") && jsonNode.has("text");

        if(isIndonesian && hasText){
            out.collect(value);
        }
    }
}
