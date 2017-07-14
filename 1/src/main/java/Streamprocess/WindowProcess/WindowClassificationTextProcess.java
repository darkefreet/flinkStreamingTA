package Streamprocess.WindowProcess;

import Model.Instances.GenericInstance;
import Model.Instances.Instance;
import Preprocess.DocumentsSVD;
import Preprocess.JSONPathTraverse;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilhelmus on 20/06/17.
 */
public class WindowClassificationTextProcess implements WindowProcessInterface {

    private XMLConfiguration config;
    private ArrayList<GenericInstance> instances;
    private static DocumentsSVD documentsSVD;
    private static transient ObjectMapper jsonParser;

    public WindowClassificationTextProcess() throws IOException, ClassNotFoundException {
        if(documentsSVD==null) {
            FileInputStream myFileInputStream = new FileInputStream("resource/svdDocuments");
            ObjectInputStream myObjectInputStream = new ObjectInputStream(myFileInputStream);
            documentsSVD = (DocumentsSVD) myObjectInputStream.readObject();
        }
        if(jsonParser == null){
            jsonParser = new ObjectMapper();
        }
        instances = new ArrayList<>();
    }

    public WindowClassificationTextProcess(XMLConfiguration _config) throws IOException, ClassNotFoundException{
        if(documentsSVD==null) {
            FileInputStream myFileInputStream = new FileInputStream("resource/svdDocuments");
            ObjectInputStream myObjectInputStream = new ObjectInputStream(myFileInputStream);
            documentsSVD = (DocumentsSVD) myObjectInputStream.readObject();
        }
        if(jsonParser == null){
            jsonParser = new ObjectMapper();
        }
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
            if(temp!=null) {
                switch (h.getString("type")) {
                    case "text": {
                        if (documentsSVD.doesHasModel()) {
                            try {
                                String res = documentsSVD.classify(temp.getValueAsText(), h.getString("svdFunction"), h.getString("target"),h.getDouble("threshold"));
                                JsonNode j = jsonParser.readTree(res);
                                testInstance.addJson("class",j);
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
