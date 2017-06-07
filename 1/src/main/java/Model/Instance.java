package Model;

import org.apache.sling.commons.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wilhelmus on 17/05/17.
 */
public class Instance {
    private String id;
    private String sentence;
    private ArrayList<Object> attributes;
    private JSONObject json;
    private HashMap<String, Integer> wordCounts;
    private long time;
    private double svdX = 0.0;
    private double svdY = 0.0;

    public Instance(String _id, String _words, String _sentence){
        this.time = System.currentTimeMillis();
        this.id = _id;
        this.wordCounts = new HashMap<>();
        this.sentence = _sentence;
        for(String word : _words.split("\\W")) {
            if(!word.isEmpty()) {
                if (this.wordCounts.containsKey(word)) {
                    this.wordCounts.put(word, this.wordCounts.get(word) + 1);
                } else {
                    this.wordCounts.put(word, 1);
                }
            }
        }
    }

    public HashMap<String, Integer> getWordCounts(){
        return this.wordCounts;
    }

    public String getId(){
        return this.id;
    }

    public long getTime(){
        return this.time;
    }

    public String getSentence(){ return this.sentence;}

    public double getSvdX(){ return svdX;}

    public double getSvdY(){ return svdY;}

    public void setSVDVariable(double _X, double _Y){
        svdX = _X;
        svdY = _Y;
    }

    public double euclideanDistance(Instance i){
        return Math.sqrt(Math.abs(svdX - i.getSvdX()) + Math.abs(svdY - i.getSvdY()));
    }

    @Override
    public String toString() {
        String ret = "{id : "+ this.id + " sentence : "+ this.sentence + "}";
        return ret;
    }
}
