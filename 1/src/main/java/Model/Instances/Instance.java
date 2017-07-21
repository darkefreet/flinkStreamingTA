package Model.Instances;

import Preprocess.JSONPathTraverse;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by wilhelmus on 17/05/17.
 */
@JsonIgnoreProperties({"json"})
public class Instance implements Serializable{
    private String id;
    //json will be ignored when transformed into a JSON String
    private JsonNode json;
    //data will not be ignored when transformed into a JSON String
    private JsonNode data;
    private long time;
    private ArrayList<Double> numericAttributes;

    public Instance(){
        this.time = System.currentTimeMillis();
        this.numericAttributes = new ArrayList<>();
        this.id = null;
        this.json = null;
        ObjectMapper jsonParser = new ObjectMapper();
        data = jsonParser.createObjectNode();
    }

    public Instance(String _id, JsonNode _json){
        this.time = System.currentTimeMillis();
        this.numericAttributes = new ArrayList<>();
        this.id = _id;
        this.json = _json;
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
            ((ObjectNode)data).put(path, dummy);
        }
    }

    public JsonNode getData(){
        return data;
    }

    public String getId(){
        return this.id;
    }

    public long getTime(){
        return this.time;
    }

    public JsonNode getJson(){return this.json;}

    public ArrayList<Double> getNumericAttributes(){
        return numericAttributes;
    }

    //ADD DATA TO data
    public void addJson(String path, JsonNode j){
        ((ObjectNode) data).put(path,j);
    }

    //ADD DATA TO data
    public void addNewData(String path, String value){
        ((ObjectNode)data).put(path,value);
    }

    public void setId(String _id){
        this.id = _id;
    }

    public void setJson(JsonNode _json){
        this.json = _json;
    }

    public void addToNumericAttributes(ArrayList<Double> _scores, Double weight)
    {
        for(Double d : _scores)
            this.numericAttributes.add(new Double(d * weight));
    }
    public void addToNumericAttributes(double _score, Double weight){
        this.numericAttributes.add(new Double(_score * weight));
    }

    public double euclideanDistance(Instance inst){
        double ret = -1.0;
        //check if the dimension is comparable
        if(numericAttributes.size()==inst.getNumericAttributes().size()){
            double squaredTotal = 0.0;
            for(int i = 0; i < numericAttributes.size();i++){
                squaredTotal+=Math.pow((numericAttributes.get(i)+inst.getNumericAttributes().get(i)),2);
            }
            ret = Math.sqrt(squaredTotal);
        }
        return ret;
    }

    @Override
    public String toString(){
        ObjectMapper jsonParser = new ObjectMapper();
        try {
            return jsonParser.writeValueAsString(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

}
