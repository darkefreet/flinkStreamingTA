package Preprocess;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Created by wilhelmus on 19/04/17.
 */

public class StreamFilter {

    private ObjectMapper jsonParser;
    private XMLConfiguration config;

    public StreamFilter(XMLConfiguration _config)
    {
        jsonParser = new ObjectMapper();
        config = _config;
    }


    public boolean filter(String value) throws IOException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }
        List<HierarchicalConfiguration> hconfig = config.configurationsAt("data.filtersConfiguration.filter");
        boolean ret = true;
        JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
        for (HierarchicalConfiguration h : hconfig){
            JsonNode temp;
            if(h.getString("attribute").contains(".")){
                String attribute = h.getString("attribute");
                String[] attributes = attribute.split("\\.");
                temp = jsonNode.get(attributes[0]);
                if(temp==null){
                    ret = false;
                    break;
                }
                if(attributes.length > 1){
                    for(int i = 1; i < attributes.length; i++){
                        temp = temp.get(attributes[i]);
                        if(temp==null) {
                            ret = false;
                            break;
                        }
                    }
                }
            }
            else{
                temp = jsonNode.get(h.getString("attribute"));
            }
            if(temp==null) {
                ret = false;
                break;
            }else{
                switch(h.getString("dataType")){
                    case "string":{
                        if(!temp.getTextValue().equals(h.getString("value")))
                            ret = false;
                        break;
                    }
                    case "count":{
                        if(!temp.isArray() && !(temp.size()>=h.getDouble("value")))
                            ret = false;
                        break;
                    }
                    default:{ //integer
                        if(!(temp.getIntValue()>=h.getInt("value")))
                            ret = false;
                    }
                }
            }
        }

        return ret;
    }
}