package Model.Instances;

import Preprocess.JSONPathTraverse;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

/**
 * Created by wilhelmus on 13/06/17.
 */
public class GenericInstance extends Instance {

    private JsonNode data;

    public GenericInstance(){
        super();
        ObjectMapper jsonParser = new ObjectMapper();
        data = jsonParser.createObjectNode();
    }

    public GenericInstance(String _id, JsonNode _json){
        super(_id,_json);
        ObjectMapper jsonParser = new ObjectMapper();
        data = jsonParser.createObjectNode();
    }

    public GenericInstance(Instance _i){
        super(_i.getId(),_i.getJson());
        ObjectMapper jsonParser = new ObjectMapper();
        data = jsonParser.createObjectNode();
    }

    public void addToData(String path){
        if(path.equals("*")){
            ((ObjectNode)data).put(path,getJson());
        }
        else {
            JSONPathTraverse JsonPath = new JSONPathTraverse();
            JsonNode dummy = JsonPath.solve(path, getJson());
            ((ObjectNode) data).put(path, dummy);
        }
    }

    public void addNewData(String path, String value){
        ((ObjectNode)data).put(path,value);
    }

    public JsonNode getData(){
        return data;
    }

}
