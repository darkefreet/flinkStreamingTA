package Model.Clustering;

import Model.Instances.TestClusteringInstance;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wilhelmus on 27/05/17.
 */
public class Cluster {
    private ArrayList<TestClusteringInstance> elements;
    private String label;
    public Cluster(){
        elements = new ArrayList<>();
        label = "noise";
    }

    public Cluster(ArrayList<TestClusteringInstance> instances){
        elements = instances;
    }

    public ArrayList<TestClusteringInstance> getElements(){
        return elements;
    }
    public void setElements(ArrayList<TestClusteringInstance> instances){
        elements = instances;
    }

    public void addInstance(TestClusteringInstance inst){
        elements.add(inst);
    }

    public String getLabel(){
        return label;
    }
    public void setLabel(String _label){
        label = _label;
    }

    public void mergeCluster(Cluster clu){
        elements.removeAll(clu.getElements());
        elements.addAll(clu.getElements());
    }

}
