package Algorithm;

import Model.Clustering.Cluster;
import Model.Instances.Instance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilhelmus on 27/05/17.
 */
public class DBSCAN {
    private double epsilon = 1f;
    private int minNumCluster = 2;
    private ArrayList<Instance> collection;
    private List<Cluster> clusters;
    ArrayList<Instance> visited;

    public DBSCAN(ArrayList<Instance> _collection, double newDistance, int numCluster){
        epsilon = newDistance;
        minNumCluster = numCluster;
        collection = _collection;
        clusters = new ArrayList<>();
    }

    public List<Cluster> getClusters() {
        return clusters;
    }
    public void setMaxDistance(double newDistance){
        epsilon = newDistance;
    }
    public void newMinCluster(int numCluster){
        minNumCluster = numCluster;
    }

    private boolean belongsToCluster(Instance P){
        boolean ret = false;
        for(Cluster c : clusters){
            if(c.getElements().contains(P)) ret = true;
        }
        return ret;
    }

    private ArrayList<Instance> getNeighbours(Instance inputValue) {
        ArrayList<Instance> neighbours = new ArrayList<>();
        for(int i=0; i<collection.size(); i++) {
            if ((inputValue.euclideanDistance(collection.get(i)) <= epsilon)) {
                neighbours.add(collection.get(i));
            }
        }
        neighbours.add(inputValue);
        return neighbours;
    }

    private void expandCluster(Instance P, Cluster c, ArrayList<Instance> neighbors){
        c.addInstance(P);
        for(int i = 0; i<neighbors.size();i++){
            if(neighbors.get(i)!=P){
                if(!visited.contains(neighbors.get(i))){
                    visited.add(neighbors.get(i));
                    ArrayList<Instance> newNeighbors = getNeighbours(neighbors.get(i));
                    if(newNeighbors.size()>= minNumCluster){
                        for(Instance j : newNeighbors){
                            if(!neighbors.contains(j)){
                                neighbors.add(j);
                            }
                        }
                    }
                }
                if(!belongsToCluster(neighbors.get(i))){
                    c.addInstance(neighbors.get(i));
                }
            }
        }
        if(c.getElements().size()>=minNumCluster){
            c.setLabel("cluster");
        }
    }

    public void performCluster(){
        visited = new ArrayList<Instance>();
        for (Instance i : collection){
            if(!visited.contains(i)){
                visited.add(i);
                ArrayList<Instance> neighbor = getNeighbours(i);
                if(neighbor.size() >= minNumCluster){
                    Cluster newCluster = new Cluster();
                    expandCluster(i,newCluster,neighbor);
                    clusters.add(newCluster);
                }
                else{
                    //noise
                    Cluster newCluster = new Cluster();
                    newCluster.addInstance(i);
                    clusters.add(newCluster);
                }
            }
        }
    }
}
