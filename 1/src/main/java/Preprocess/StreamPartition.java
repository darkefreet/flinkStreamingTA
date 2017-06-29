package Preprocess;

import Preprocess.Calculation.SQLLikeFilter;
import org.apache.commons.configuration.XMLConfiguration;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wilhelmus on 27/06/17.
 */
public class StreamPartition {
    private ObjectMapper jsonParser;
    private XMLConfiguration config;
    private JSONPathTraverse jsonTraverse;

    public StreamPartition(){
        jsonParser = new ObjectMapper();
        jsonTraverse = new JSONPathTraverse();
    }

    public StreamPartition(XMLConfiguration _config){
        jsonParser = new ObjectMapper();
        config = _config;
        jsonTraverse = new JSONPathTraverse();
    }

    public void setConfig(XMLConfiguration _config){
        config = _config;
    }

    public ArrayList<JsonNode> partition(String json) throws IOException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }
        ArrayList<JsonNode> ret = new ArrayList<>();
        JsonNode temp = jsonParser.readTree(json);
        String path = config.getString("data.partitionConfiguration.path");
        JsonNode arrayTemp = jsonTraverse.solve(path,temp);
        if(arrayTemp.isArray()){
            for(JsonNode j : arrayTemp){
                ret.add(j);
            }
        }
        return ret;
    }



}
