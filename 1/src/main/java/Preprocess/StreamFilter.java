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

    public StreamFilter(XMLConfiguration _config)
    {
        jsonParser = new ObjectMapper();
        config = _config;
        sqlFilter = new SQLLikeFilter();
    }


    public boolean filter(String value) throws IOException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }
        String configuration = config.getString("dataTransformation.filter");
        JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
        boolean ret = sqlFilter.solve(configuration,jsonNode);
        return ret;
    }
}
