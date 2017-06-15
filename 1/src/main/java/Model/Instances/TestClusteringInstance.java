package Model.Instances;

import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;

/**
 * Created by wilhelmus on 13/06/17.
 */
public class TestClusteringInstance extends Instance {

    private String text;

    public TestClusteringInstance(){
        super();
        text = getJson().get("text").getValueAsText();
    }

    public TestClusteringInstance(String _id, JsonNode _json){
        super(_id,_json);
        text = getJson().get("text").getValueAsText();
    }

    public TestClusteringInstance(Instance _i){
        super(_i.getId(),_i.getJson());
        text = getJson().get("text").getValueAsText();
    }

    public String getText(){
        return text;
    }

}
