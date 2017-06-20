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
import java.util.ArrayList;

/**
 * Created by wilhelmus on 17/05/17.
 */
public class StreamParser implements FlatMapFunction<String, Instance> {

    private static transient ArrayList<XMLConfiguration> configs;
    private transient ObjectMapper jsonParser;
    private int configIndex;

    public StreamParser(ArrayList<XMLConfiguration> _configs, int index) throws ConfigurationException {
        configs = _configs;
        configIndex = index;
    }

    @Override
    public void flatMap(String value, Collector<Instance> out) throws IOException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }

        switch(configs.get(configIndex).getString("data.type")){
            case "json":{
                JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
                StreamFilter streamFilter = new StreamFilter(configs.get(configIndex));
                if(streamFilter.filter(value)){
                    if(jsonNode!=null) {
                        Instance inst = new Instance(jsonNode.get(configs.get(configIndex).getString("data.id")).toString(), jsonNode);
                        out.collect(inst);
                    }
                }
                break;
            }
            default:{
                out.collect(null);
            }
        }

    }

}
