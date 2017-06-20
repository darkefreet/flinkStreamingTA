package Model.Instances;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by wilhelmus on 17/05/17.
 */
@JsonIgnoreProperties({"json"})
public class Instance {
    private String id;
    private JsonNode json;
    private long time;
    private ArrayList<Double> numericAttributes;

    public Instance(){
        this.time = System.currentTimeMillis();
        this.numericAttributes = new ArrayList<>();
        this.id = null;
        this.json = null;
    }

    public Instance(String _id, JsonNode _json){
        this.time = System.currentTimeMillis();
        this.numericAttributes = new ArrayList<>();
        this.id = _id;
        this.json = _json;
    }

    public String getId(){
        return this.id;
    }

    public long getTime(){
        return this.time;
    }

    public JsonNode getJson(){return this.json;}

    public void setId(String _id){
        this.id = _id;
    }

    public void setJson(JsonNode _json){
        this.json = _json;
    }

    public ArrayList<Double> getNumericAttributes(){
        return numericAttributes;
    }

    public void addToNumericAttributes(ArrayList<Double> _scores, Double weight)
    {
        for(Double d : _scores)
            this.numericAttributes.add(new Double(d * weight));
    }
    public void addToNumericAttributes(double _score, Double weight){
        this.numericAttributes.add(new Double(_score * weight));
    }

    public double euclideanDistance(GenericInstance inst){
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

}
