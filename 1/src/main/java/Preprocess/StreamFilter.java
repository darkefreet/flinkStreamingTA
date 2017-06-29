package Preprocess;

import Preprocess.Calculation.SQLLikeFilter;
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
    private SQLLikeFilter sqlFilter;
    private JSONPathTraverse jsonTraverse;

    public StreamFilter(XMLConfiguration _config)
    {
        jsonParser = new ObjectMapper();
        config = _config;
        sqlFilter = new SQLLikeFilter();
        jsonTraverse = new JSONPathTraverse();
    }


    public boolean filter(String value) throws IOException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }
        List<HierarchicalConfiguration> hconfig = config.configurationsAt("data.filtersConfiguration.filter");
        boolean ret = true;
        JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
        for (HierarchicalConfiguration h : hconfig){
            JsonNode temp = jsonTraverse.solve(h.getString("attribute"),jsonNode);
            if(temp==null){
                ret = false;
                break;
            }
            else {
                switch (h.getString("dataType")) {
                    case "string": {
                        if (!temp.getTextValue().equals(h.getString("value")))
                            ret = false;
                        break;
                    }
                    case "integer" :{
                        if(!sqlFilter.compareNumber(temp.getDoubleValue(),h.getDouble("value"),h.getString("comparison")))
                            ret = false;
                        break;
                    }
                    case "count":{
                        if(temp.isArray()) {
                            if (!sqlFilter.compareNumber(temp.size(), h.getDouble("value"), h.getString("comparison")))
                                ret = false;
                        }
                        else{
                            ret = false;
                        }
                    break;
                    }
                }
            }
        }
        return ret;
    }
}
