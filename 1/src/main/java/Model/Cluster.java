package Model;

import java.util.ArrayList;

/**
 * Created by wilhelmus on 27/05/17.
 */
public class Cluster {
    private ArrayList<Instance> elements;
    private String label;
    public Cluster(){
        elements = new ArrayList<>();
        label = "noise";
    }

    public Cluster(ArrayList<Instance> instances){
        elements = instances;
    }

    public ArrayList<Instance> getElements(){
        return elements;
    }
    public void setElements(ArrayList<Instance> instances){
        elements = instances;
    }

    public void addInstance(Instance inst){
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

    @Override
    public String toString(){
        String ret = "[{ label : " +label +"},{ data : ";
        for(int i = 0; i<elements.size();i++){
            ret+=elements.get(i).toString();
            if(i!=elements.size()-1)
                ret+=",";
        }
        ret+="}]\n";
        return ret;
    }

}
