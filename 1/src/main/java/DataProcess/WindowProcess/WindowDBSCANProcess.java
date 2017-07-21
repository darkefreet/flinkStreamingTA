package DataProcess.WindowProcess;

import Algorithm.DBSCAN;
import Model.Instances.Instance;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wilhelmus on 15/06/17.
 */
public class WindowDBSCANProcess implements WindowProcessInterface{

    private ArrayList<Instance> instances;

    public WindowDBSCANProcess(){
        instances = new ArrayList<>();
    }


    @Override
    public String processData() {
        String ret = null;
        if(instances.size()>1) {
            ObjectMapper jsonParser = new ObjectMapper();
            DBSCAN dbscanner = new DBSCAN(instances, 20000.0, 3);
            dbscanner.performCluster();
            try {
                ret = jsonParser.writeValueAsString(dbscanner.getClusters());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public void preProcessData(Instance inst) {
        instances.add(inst);
    }


}
