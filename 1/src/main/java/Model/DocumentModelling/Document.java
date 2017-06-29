package Model.DocumentModelling;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by wilhelmus on 10/06/17.
 */
public class Document implements Serializable{
    private String label;
    private HashMap<String, Integer> wordCounts;
    private String resource;

    static final int MAX_TERMS = 10;

    public Document(){
        wordCounts = new HashMap<>();
    }

    public void setLabel(String _label){
        this.label = _label;
    }

    public void setResource(String _resource){
        this.resource = _resource;
    }

    public void countWords(){
        if(!this.resource.equals(null)){
            for(String word:this.resource.split("\\W")){
                if (wordCounts.containsKey(word)) {
                    if(wordCounts.get(word) < MAX_TERMS)
                        wordCounts.put(word, wordCounts.get(word) + 1);
                } else {
                    wordCounts.put(word, 1);
                }
            }
        }
    }

    public String getLabel(){
        return this.label;
    }

    public String getResource(){
        return this.resource;
    }

    public HashMap<String, Integer> getWordCounts(){
        return this.wordCounts;
    }
}
