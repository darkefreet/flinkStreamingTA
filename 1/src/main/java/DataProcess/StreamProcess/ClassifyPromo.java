package DataProcess.StreamProcess;

import Model.Instances.Instance;
import Preprocess.DocumentsSVD;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by wilhelmus on 20/07/17.
 */
public class ClassifyPromo implements StreamProcessInterface {
    private DocumentsSVD documentsSVD;
    private ObjectMapper jsonParser;

    public ClassifyPromo() throws IOException, ClassNotFoundException {
        if(documentsSVD==null) {
            FileInputStream myFileInputStream = new FileInputStream("resource/svdDocuments");
            ObjectInputStream myObjectInputStream = new ObjectInputStream(myFileInputStream);
            documentsSVD = (DocumentsSVD) myObjectInputStream.readObject();
        }
        if(jsonParser == null){
            jsonParser = new ObjectMapper();
        }
    }

    @Override
    public ArrayList<Instance> transform(Instance inst){
        ArrayList<Instance> ret = new ArrayList<>();
        if(documentsSVD.doesHasModel()){
            try {
                String res = documentsSVD.classify(inst.getJson().get("text").getValueAsText(), "dot", "promo", 1.0);
                JsonNode j = jsonParser.readTree(res);
                inst.addJson("class",j);
                ret.add(inst);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

}
