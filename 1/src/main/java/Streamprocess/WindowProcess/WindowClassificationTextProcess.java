package Streamprocess.WindowProcess;

import Model.Instances.GenericInstance;
import Model.Instances.Instance;
import Preprocess.DocumentsSVD;
import Preprocess.JSONPathTraverse;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilhelmus on 20/06/17.
 */
public class WindowClassificationTextProcess implements WindowProcessInterface {

    private XMLConfiguration config;
    private ArrayList<GenericInstance> instances;
    private DocumentsSVD documentsSVD;

    public WindowClassificationTextProcess(){
        instances = new ArrayList<>();
    }

    public WindowClassificationTextProcess(XMLConfiguration _config, DocumentsSVD _doc){
        documentsSVD = _doc;
        config = _config;
        instances = new ArrayList<>();
    }

    @Override
    public String processData() {
        String ret = null;
        if(instances.size()>0) {
            ObjectMapper jsonParser = new ObjectMapper();
            try {
                ret = jsonParser.writeValueAsString(instances);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public void preProcessData(Instance inst) {
        List<HierarchicalConfiguration> hconfig = config.configurationsAt("dataMining.attributes.attribute");
        GenericInstance testInstance = new GenericInstance(inst);
        JSONPathTraverse jsonPath = new JSONPathTraverse();
        for (HierarchicalConfiguration h : hconfig) {
            JsonNode temp = jsonPath.solve(h.getString("path"),inst.getJson());
            if(!temp.equals(null)) {
                switch (h.getString("type")) {
                    case "text": {
                        if (documentsSVD.doesHasModel()) {
                            testInstance.addToNumericAttributes(documentsSVD.search(temp.getTextValue(), h.getString("svdFunction")), h.getDouble("weight"));
                            try {
                                testInstance.addNewData("class",documentsSVD.classify(temp.getTextValue(), h.getString("svdFunction")));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
        String[] formatOutputs = config.getStringArray("dataMining.resultsData.path");
        for(String form : formatOutputs){
            testInstance.addToData(form);
        }
        instances.add(testInstance);
    }

    @Override
    public void setConfig(XMLConfiguration _config) {
        config = _config;
    }

    public void setDocumentsSVD(DocumentsSVD doc) {
        documentsSVD = doc;
    }
}
