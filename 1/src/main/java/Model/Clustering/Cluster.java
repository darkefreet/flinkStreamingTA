package Model.Clustering;

/**
 * Created by wilhelmus on 23/11/17.
 */
import Model.Instances.Instance;
import java.util.ArrayList;

public class Cluster {
    private ArrayList<Instance> elements;
    private String label;

    public Cluster() {
        this.elements = new ArrayList();
        this.label = "noise";
    }

    public Cluster(ArrayList<Instance> instances) {
        this.elements = instances;
    }

    public ArrayList<Instance> getElements() {
        return this.elements;
    }

    public void setElements(ArrayList<Instance> instances) {
        this.elements = instances;
    }

    public void addInstance(Instance inst) {
        this.elements.add(inst);
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String _label) {
        this.label = _label;
    }

    public void mergeCluster(Cluster clu) {
        this.elements.removeAll(clu.getElements());
        this.elements.addAll(clu.getElements());
    }
}