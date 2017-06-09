package Model;

import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wilhelmus on 17/05/17.
 */
public class Instance {
    private String id;
    private String sentence;
    private ArrayList<Object> attributes;
    private JsonNode json;
    private HashMap<String, Integer> wordCounts;
    private long time;

    public Instance(String _id, JsonNode _json){
        this.time = System.currentTimeMillis();
        this.id = _id;
        this.wordCounts = new HashMap<>();
        this.json = _json;
        this.sentence = this.json.get("text").getTextValue();
//        for(String word : _words.split("\\W")) {
//            if(!word.isEmpty()) {
//                if (this.wordCounts.containsKey(word)) {
//                    this.wordCounts.put(word, this.wordCounts.get(word) + 1);
//                } else {
//                    this.wordCounts.put(word, 1);
//                }
//            }
//        }
    }

    public String getId(){
        return this.id;
    }

    public long getTime(){
        return this.time;
    }


    public double euclideanDistance(Instance i){
        return 0.0;
    }

    @Override
    public String toString() {
        String ret = "test";
        return ret;
    }
}
