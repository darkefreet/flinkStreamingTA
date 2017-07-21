package DataProcess.StreamProcess;

import Model.Instances.Instance;
import org.codehaus.jackson.JsonNode;


import java.util.ArrayList;

/**
 * Created by wilhelmus on 21/07/17.
 */
public class TransactionPartition implements StreamProcessInterface {
    @Override
    public ArrayList<Instance> transform(Instance inst) {
        ArrayList<Instance> ret = new ArrayList<>();
        JsonNode arrayTemp = inst.getJson().get("x").get("out");
        if(arrayTemp.isArray()){
            if(arrayTemp.isArray()){
                for(JsonNode j : arrayTemp){
                    Instance a = new Instance(null,j);
                    ret.add(a);
                }
            }
        }
        return ret;
    }
}
