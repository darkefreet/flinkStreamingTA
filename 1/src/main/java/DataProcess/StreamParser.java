package DataProcess;

import Model.Instances.Instance;
import Preprocess.JSONPathTraverse;
import Preprocess.StreamFilter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by wilhelmus on 17/05/17.
 */
public class StreamParser implements FlatMapFunction<String, Instance> {

    private static transient XMLConfiguration config;
    private static transient ObjectMapper jsonParser;
    private static transient JSONPathTraverse jsonPathTraverse;

    public StreamParser(XMLConfiguration _config) throws ConfigurationException {
        config = _config;
        jsonPathTraverse = new JSONPathTraverse();
    }

    @Override
    public void flatMap(String value, Collector<Instance> out) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if(jsonParser ==null){
            jsonParser = new ObjectMapper();
        }
        switch(config.getString("dataTransformation.type")){
            case "json":{
                JsonNode jsonNode = jsonParser.readValue(value,JsonNode.class);
                StreamFilter streamFilter = new StreamFilter(config);
                if(streamFilter.filter(value)){
                    if(jsonNode!=null) {
                        String path = config.getString("dataTransformation.id");
                        if(jsonNode!=null && path!=null) {
                            Instance inst = new Instance(null, jsonNode);
                            if(config.getString("dataTransformation.processingClass")!=null && !config.getString("dataTransformation.processingClass").equals("")){
                                Class cl = Class.forName(config.getString("dataTransformation.processingClass"));
                                Constructor con = cl.getConstructor();
                                Object obj = con.newInstance();
                                ArrayList<Instance> res = ArrayList.class.cast(obj.getClass().getDeclaredMethod("transform",inst.getClass()).invoke(obj,inst));
                                if(res!=null){
                                    for(Instance i:res){
                                        if(jsonPathTraverse.solve(path,i.getJson())!=null)
                                            i.setId(jsonPathTraverse.solve(path,i.getJson()).getValueAsText());
                                        if(config.configurationAt("dataTransformation.numericAttributes").getBoolean("[@status]")){
                                            List<HierarchicalConfiguration> hconfigs = config.configurationsAt("dataTransformation.numericAttributes.attribute");
                                            for(HierarchicalConfiguration hcon : hconfigs){
                                                JsonNode temp = jsonPathTraverse.solve(hcon.getString(""),i.getJson());
                                                if(temp!=null) {
                                                    i.addToNumericAttributes(temp.getDoubleValue(), hcon.getDouble("[@weight]"));
                                                }
                                            }
                                        }
                                        if(config.getString("dataTransformation.dataIncluded.path")!=null){
                                            String[] dataIncluded = config.getStringArray("dataTransformation.dataIncluded.path");
                                            for (String s : dataIncluded) {
                                                if(s!=null && !s.equals(""))
                                                    i.addToData(s);
                                            }
                                        }
                                        out.collect(i);
                                    }
                                }
                            }else{
                                if(jsonPathTraverse.solve(path,jsonNode)!=null)
                                    inst.setId(jsonPathTraverse.solve(path,jsonNode).getValueAsText());
                                if(config.configurationAt("dataTransformation.numericAttributes").getBoolean("[@status]")){
                                    List<HierarchicalConfiguration> hconfigs = config.configurationsAt("dataTransformation.numericAttributes.attribute");
                                    for(HierarchicalConfiguration con : hconfigs){
                                        JsonNode temp = jsonPathTraverse.solve(con.getString(""),inst.getJson());
                                        if(temp!=null) {
                                            inst.addToNumericAttributes(temp.getDoubleValue(), con.getDouble("[@weight]"));
                                        }
                                    }
                                }
                                if(config.getString("dataTransformation.dataIncluded.path")!=null){
                                    String[] dataIncluded = config.getStringArray("dataTransformation.dataIncluded.path");
                                    for (String s : dataIncluded) {
                                        if(s!=null && !s.equals(""))
                                            inst.addToData(s);
                                    }
                                }
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
