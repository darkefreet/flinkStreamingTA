package Preprocess;

import Model.DocumentModelling.ClassifyDocumentsResult;
import Model.DocumentModelling.Document;
import com.aliasi.matrix.SvdMatrix;
import org.apache.flink.api.java.tuple.Tuple2;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

/**
 * Created by wilhelmus on 07/06/17.
 */
public class DocumentsSVD implements Serializable{

    private double termDocumentMatrix[][];
    private ArrayList<Document> documents;
    private Set<String> setOfTerms;
    private Set<String> labels;
    private String[] terms;
    private transient SvdMatrix svdMatrix;
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
    private double[][] docVectors;
    private boolean hasModel;

    static final int NUM_FACTORS = 2;

    public DocumentsSVD(){
        //initialize value
        maxFactors = 2;
        featureInit = 0.01;
        initialLearningRate = 0.005;
        annealingRate = 1000;
        regularization = 0.00;
        minImprovement = 0.0000;
        minEpochs = 10;
        maxEpochs = 50000;
        svdMatrix = null;
        scales = null;
        termVectors = null;
        docVectors = null;
        documents = new ArrayList<>();
        setOfTerms = new HashSet<>();
        labels = new HashSet<>();
        hasModel = false;
    }

    public boolean doesHasModel(){
        return hasModel;
    }

    public void makeSVDModel(String path,String label, String isi) throws IOException {
        hasModel = true;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        ObjectMapper jsonParser = new ObjectMapper();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                URL url = new URL("file:"+file.getCanonicalPath());
                JsonNode json = jsonParser.readValue(url, JsonNode.class);
                Document doc= new Document();
                doc.setLabel(json.get(label).getTextValue());
                String res = json.get(isi).getTextValue().toLowerCase();
                NormalizeSentence normalize = new NormalizeSentence(res);
                doc.setResource(normalize.getSentence());
                doc.countWords();
                labels.add(json.get(label).getTextValue());
                documents.add(doc);
            }
        }

        //COLLECT ALL THE TERMS AND COUNT TERMS PER DOCUMENT
        for(Document doc: documents){
            for(String s:doc.getWordCounts().keySet()){
                setOfTerms.add(s);
            }
            terms = setOfTerms.toArray(new String[setOfTerms.size()]);
        }

        //MAKE TERM DOCUMENT MATRIX
        termDocumentMatrix = new double[terms.length][documents.size()];
        int i = 0;
        for (String term : terms) {
            for (int j = 0; j < documents.size(); j++) {
                if (documents.get(j).getWordCounts().containsKey(term)) {
                    termDocumentMatrix[i][j] = documents.get(j).getWordCounts().get(term);
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
//        printTermDocumentMatrix();

    }
    public void printTermDocumentMatrix(){
        if(!termDocumentMatrix.equals(null) && hasModel){
            for(int i = 0; i<termDocumentMatrix.length;i++){
                System.out.print("[ ");
                for(int j = 0; j<termDocumentMatrix[i].length-1;j++){
                    System.out.print(termDocumentMatrix[i][j] + " , ");
                }
                System.out.println(termDocumentMatrix[i][termDocumentMatrix[i].length-1] + " ]");
            }
        }
    }

    private double dotProduct(double[] xs, double[] ys, double[] scales) {
        double sum = 0.0;
        for (int k = 0; k < xs.length; ++k)
            sum += xs[k] * ys[k] * scales[k];
        return sum;
    }

    private double cosine(double[] xs, double[] ys, double[] scales) {
        double product = 0.0;
        double xsLengthSquared = 0.0;
        double ysLengthSquared = 0.0;
        for (int k = 0; k < xs.length; ++k) {
            double sqrtScale = Math.sqrt(scales[k]);
            double scaledXs = sqrtScale * xs[k];
            double scaledYs = sqrtScale * ys[k];
            xsLengthSquared += scaledXs * scaledXs;
            ysLengthSquared += scaledYs * scaledYs;
            product += scaledXs * scaledYs;
        }
        return product / Math.sqrt(xsLengthSquared * ysLengthSquared);
    }

    public String classify(String arg, String function) throws IOException {
        String ret = "";
        if(hasModel) {
            NormalizeSentence normalize = new NormalizeSentence(arg.toLowerCase());
            String[] queryTerms = normalize.getSentence().split(" |,"); // space or comma separated
            double[] queryVector = new double[NUM_FACTORS];
            Arrays.fill(queryVector, 0.0);
            for (String term : queryTerms) {
                addTermVector(term, queryVector);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            List<ClassifyDocumentsResult> a = new ArrayList<>();
            Map<String, Tuple2<Double,Integer>> totals = new HashMap<>();
            for(String label : labels){
                Tuple2<Double,Integer> tup = new Tuple2<>();
                tup.setField(0.0,0);tup.setField(0,1);
                totals.put(label,tup);
            }
            for (int j = 0; j < docVectors.length; ++j) {
                double score;
                String label = documents.get(j).getLabel();
                if(function.equals("dot")) {
                    score = dotProduct(queryVector, docVectors[j], scales);
                    if(!Double.isNaN(score)) {
                        if(score > totals.get(label).f0){
                            totals.get(label).setField(score, 0);
                        }
                    }
                }else {
                    score = cosine(queryVector, docVectors[j], scales);
                    if(!Double.isNaN(score)) {
                        totals.get(label).setField(totals.get(label).f0+score,0);
                        totals.get(label).setField(totals.get(label).f1+1,1);
                    }
                }
            }

            for(String k : totals.keySet()){
                if(function.equals("dot")) {
                    ClassifyDocumentsResult c = new ClassifyDocumentsResult(totals.get(k).f0, k);
                    a.add(c);
                    ret = objectMapper.writeValueAsString(a);
                }
                else{
                    if(totals.get(k).f1<1)
                        totals.get(k).setField(1,1);
                    ClassifyDocumentsResult c = new ClassifyDocumentsResult(totals.get(k).f0/totals.get(k).f1, k);
                    a.add(c);
                    ret = objectMapper.writeValueAsString(a);
                }
            }
        }
        return ret;
    }

    public String classify(String arg, String function,String target) throws IOException {
        String ret = "";
        if(hasModel) {
            NormalizeSentence normalize = new NormalizeSentence(arg.toLowerCase());
            String[] queryTerms = normalize.getSentence().split(" |,"); // space or comma separated
            double[] queryVector = new double[NUM_FACTORS];
            Arrays.fill(queryVector, 0.0);
            for (String term : queryTerms) {
                addTermVector(term, queryVector);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Tuple2<Double,Integer>> totals = new HashMap<>();
            for(String label : labels){
                Tuple2<Double,Integer> tup = new Tuple2<>();
                tup.setField(0.0,0);tup.setField(0,1);
                totals.put(label,tup);
            }
            for (int j = 0; j < docVectors.length; ++j) {
                double score;
                String label = documents.get(j).getLabel();
                if(function.equals("dot")) {
                    score = dotProduct(queryVector, docVectors[j], scales);
                    if(!Double.isNaN(score)) {
                        if(score > totals.get(label).f0){
                            totals.get(label).setField(score, 0);
                        }
                    }
                }else {
                    score = cosine(queryVector, docVectors[j], scales);
                    if(!Double.isNaN(score)) {
                        totals.get(label).setField(totals.get(label).f0+score,0);
                        totals.get(label).setField(totals.get(label).f1+1,1);
                    }
                }
            }

            if(function.equals("dot")) {
                ClassifyDocumentsResult c = new ClassifyDocumentsResult(totals.get(target).f0, target);
                ret = objectMapper.writeValueAsString(c);
            }else{
                if(totals.get(target).f1<1)
                    totals.get(target).setField(1,1);
                ClassifyDocumentsResult c = new ClassifyDocumentsResult(totals.get(target).f0/totals.get(target).f1, target);
                ret = objectMapper.writeValueAsString(c);
            }

        }
        return ret;
    }

    public ArrayList<Double> search(String arg,String function){
        ArrayList<Double> ret = new ArrayList<>();
        if(hasModel) {
            NormalizeSentence normalize = new NormalizeSentence(arg.toLowerCase());
            String[] queryTerms = normalize.getSentence().split(" |,"); // space or comma separated
            double[] queryVector = new double[NUM_FACTORS];
            Arrays.fill(queryVector, 0.0);
            for (String term : queryTerms) {
                addTermVector(term, queryVector);
            }
            for (int j = 0; j < docVectors.length; ++j) {
                double score;
                if(function.equals("dot")) {
                    score = dotProduct(queryVector, docVectors[j], scales);
                }else {
                    score = cosine(queryVector, docVectors[j], scales);
                }
                ret.add(new Double(score));
            }
        }
        return ret;
    }

    private void addTermVector(String term, double[] queryVector) {
        int i = 0;
        for (String t : terms) {
            if (t.equals(term)) {
                for (int j = 0; j < NUM_FACTORS; ++j) {
                    queryVector[j] += termVectors[i][j];
                }
                return;
            }
            i++;
        }
    }



}
