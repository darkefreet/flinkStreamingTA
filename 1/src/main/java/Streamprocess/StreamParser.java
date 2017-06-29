package Streamprocess;

import Model.Instances.Instance;
import Preprocess.JSONPathTraverse;
import Preprocess.StreamFilter;

import Preprocess.StreamPartition;
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
    private static transient ObjectMapper jsonParser;
    private int configIndex;
    private static transient JSONPathTraverse pathTraverse;

    public StreamParser(ArrayList<XMLConfiguration> _configs, int index) throws ConfigurationException {
        configs = _configs;
        configIndex = index;
        pathTraverse = new JSONPathTraverse();
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
                        if(configs.get(configIndex).getString("data.partitionConfiguration.anyPartition").equals("true")){
                            StreamPartition part= new StreamPartition(configs.get(configIndex));
                            for(JsonNode j: part.partition(value)){
                                String path = configs.get(configIndex).getString("data.id");
                                Instance inst = new Instance(pathTraverse.solve(path,j).getTextValue(),j);
                                out.collect(inst);
                            }
                        }else {
                            String path = configs.get(configIndex).getString("data.id");
                            if(jsonNode!=null && path!=null) {
                                Instance inst = new Instance(null, jsonNode);
                                out.collect(inst);
                            }
                        }
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
