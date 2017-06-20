package Model.Clustering;

import Model.Instances.GenericInstance;

import java.util.ArrayList;

/**
 * Created by wilhelmus on 27/05/17.
 */
public class Cluster {
    private ArrayList<GenericInstance> elements;
    private String label;
    public Cluster(){
        elements = new ArrayList<>();
        label = "noise";
    }

    public Cluster(ArrayList<GenericInstance> instances){
        elements = instances;
    }

    public ArrayList<GenericInstance> getElements(){
        return elements;
    }
    public void setElements(ArrayList<GenericInstance> instances){
        elements = instances;
    }

    public void addInstance(GenericInstance inst){
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
