package Model;

import com.aliasi.matrix.SvdMatrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wilhelmus on 17/05/17.
 */
public class ClusterResult {

    private double termDocumentMatrix[][];
    private ArrayList<Instance> instances;
    private SvdMatrix svdMatrix;
    private int maxFactors;
    private double featureInit;
    private double initialLearningRate;
    private int annealingRate;
    private double regularization;
    private double minImprovement;
    private int minEpochs;
    private int maxEpochs;
    private double[] scales;
    private double[][] termVectors;
    double[][] docVectors;
    private ArrayList<Cluster> clusters;


    public ClusterResult(){
        maxFactors = 2;
        featureInit = 0.01;
        initialLearningRate = 0.005;
        annealingRate = 1000;
        regularization = 0.00;
        minImprovement = 0.0000;
        minEpochs = 10;
        maxEpochs = 50000;
        instances = new ArrayList<Instance>();
        svdMatrix = null;
        scales = null;
        termVectors = null;
        docVectors = null;
        clusters = new ArrayList<Cluster>();
    }

    public void addInstance(Instance i){
        this.instances.add(i);
    }

    public void calculateSVD(){
        System.out.println("Jumlah instances : " + instances.size());
        if(instances.size()>0 ) {
            Set<String> terms = new HashSet<String>();
            for (Instance i : instances) {
                for (String s : i.getWordCounts().keySet()) {
                    terms.add(s);
                }
            }
            termDocumentMatrix = new double[terms.size()][instances.size()];
            int i = 0;
            for (String term : terms) {
                for (int j = 0; j < instances.size(); j++) {
                    if (instances.get(j).getWordCounts().containsKey(term)) {
                        termDocumentMatrix[i][j] = instances.get(j).getWordCounts().get(term);
                    } else {
                        termDocumentMatrix[i][j] = 0;
                    }
                }
                i++;
            }
            svdMatrix = SvdMatrix.svd(termDocumentMatrix, maxFactors, featureInit, initialLearningRate, annealingRate, regularization, null, minImprovement, minEpochs, maxEpochs);
            scales = svdMatrix.singularValues();
            termVectors = svdMatrix.leftSingularVectors();
            docVectors = svdMatrix.rightSingularVectors();
            for(int k = 0; k < instances.size();k++){
                instances.get(k).setSVDVariable(docVectors[k][0],docVectors[k][1]);
            }
        }else{
            svdMatrix = null;
            scales = null;
            termVectors = null;
            docVectors = null;
        }
    }

    public ArrayList<Instance> getInstances(){return instances;}

    public void setClusters(ArrayList<Cluster> c){
        clusters = c;
    }

    @Override
    public String toString() {
        String ret ="Jumlah cluster = "+clusters.size() +"\n";
        for(Cluster c:clusters){
            ret+=c.toString();
        }
        return ret;
    }
}
