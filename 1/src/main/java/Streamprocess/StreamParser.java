package Streamprocess;

import Model.Instances.Instance;
import Preprocess.StreamFilter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by wilhelmus on 17/05/17.
 */
public class StreamParser implements FlatMapFunction<String, Instance> {

    private static transient XMLConfiguration config;
    private transient ObjectMapper jsonParser;

    public StreamParser(XMLConfiguration _config) throws ConfigurationException {
        config = _config;
    }

    @Override
    public void flatMap(String value, Collector<Instance> out) throws IOException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }

        switch(config.getString("data.type")){
            case "tweet":{
                JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
                StreamFilter streamFilter = new StreamFilter(config);
                if(streamFilter.filter(value)){
                    out.collect(new Instance(jsonNode.get(config.getString("data.id")).toString(),jsonNode));
                }
                break;
            }
            default:{
                out.collect(null);
            }
        }

    }

}
