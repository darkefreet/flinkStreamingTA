package Streamprocess;

import Algorithm.DBSCAN;
import Model.Instances.Instance;
import Model.Instances.TestClusteringInstance;
import Preprocess.DocumentsSVD;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilhelmus on 23/05/17.
 */
public class WindowStreamProcess implements WindowFunction<Instance, String, String, TimeWindow> {

    private static transient DocumentsSVD documentsSVD;
    private static transient XMLConfiguration config;

    public WindowStreamProcess(DocumentsSVD _doc, XMLConfiguration _config) throws ConfigurationException {
        config = _config;
        documentsSVD = _doc;
    }

    @Override
    public void apply(String s, TimeWindow timeWindow, Iterable<Instance> iterable, Collector<String> collector) throws Exception {
        Logger LOG = LoggerFactory.getLogger("log Test");
        ObjectMapper jsonParser = new ObjectMapper();
        List<HierarchicalConfiguration> hconfig = config.configurationsAt("dataMining.attributes.attribute");
        ArrayList<TestClusteringInstance> testInstances = new ArrayList<>();
        for (Instance inst : iterable) {
            TestClusteringInstance testInstance = new TestClusteringInstance(inst);
            JsonNode jsonNode = inst.getJson();
            for (HierarchicalConfiguration h : hconfig) {
                JsonNode temp = null;
                if (h.getString("path").contains(".")) {
                    String[] attributes = h.getString("path").split("\\.");
                    temp = jsonNode.get(attributes[0]);
                    if (attributes.length > 1) {
                        for (int i = 1; i < attributes.length; i++) {
                            temp = temp.get(attributes[i]);
                            if (temp.equals(null)) break;
                        }
                    }
                } else {
                    temp = jsonNode.get(h.getString("path"));
                }

                if(!temp.equals(null)) {
                    switch (h.getString("type")) {
                        case "text": {
                            if (documentsSVD.doesHasModel()) {
                                testInstance.addToNumericAttributes(documentsSVD.search(temp.getTextValue(), h.getString("svdFunction")), h.getDouble("weight"));
                            }
                            break;
                        }
                        default: { //only numeric
                            testInstance.addToNumericAttributes(temp.getDoubleValue(), h.getDouble("weight"));
                            break;
                        }
                    }
                }
            }
            testInstances.add(testInstance);
        }

        DBSCAN dbscanner = new DBSCAN(testInstances, config.getDouble("dataMining.maxDistance"), config.getInt("dataMining.minClusters"));
        dbscanner.performCluster();

        String ret = jsonParser.writeValueAsString(dbscanner.getClusters());
        collector.collect(ret);
    }

}
