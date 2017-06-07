package Preprocess;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by wilhelmus on 19/04/17.
 */
public class TweetFilter{

    private ObjectMapper jsonParser;

    public TweetFilter(){
        jsonParser = new ObjectMapper();
    }

    public boolean isIndonesian(String value) throws IOException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }
        JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
        boolean isIndonesian = jsonNode.has("user") && jsonNode.get("user").get("lang").getTextValue().equals("id");
        boolean hasText = jsonNode.has("created_at") && jsonNode.has("text");

        if(isIndonesian && hasText)
            return true;
        else return false;
    }
}
