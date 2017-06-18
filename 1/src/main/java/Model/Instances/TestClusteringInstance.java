package Model.Instances;

import Preprocess.JSONPathTraverse;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

/**
 * Created by wilhelmus on 13/06/17.
 */
public class TestClusteringInstance extends Instance {

    private JsonNode data;

    public TestClusteringInstance(){
        super();
        ObjectMapper jsonParser = new ObjectMapper();
        data = jsonParser.createObjectNode();
    }

    public TestClusteringInstance(String _id, JsonNode _json){
        super(_id,_json);
        ObjectMapper jsonParser = new ObjectMapper();
        data = jsonParser.createObjectNode();
    }

    public TestClusteringInstance(Instance _i){
        super(_i.getId(),_i.getJson());
        ObjectMapper jsonParser = new ObjectMapper();
        data = jsonParser.createObjectNode();
    }

    public void addToData(String path){
        JSONPathTraverse JsonPath = new JSONPathTraverse();
        JsonNode dummy = JsonPath.solve(path,getJson());
        ((ObjectNode)data).put(path,dummy);
    }

    public JsonNode getData(){
        return data;
    }

}
