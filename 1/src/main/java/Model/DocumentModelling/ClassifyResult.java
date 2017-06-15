package Model.DocumentModelling;

import org.apache.flink.api.java.tuple.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wilhelmus on 13/06/17.
 */
public class ClassifyResult {
    private String label;
    private double score;

    public ClassifyResult(){
    }

    public ClassifyResult(double _score, String _label){
        score = _score;
        label = _label;
    }

    public String getLabel(){
        return label;
    }
    public double getScore(){
        return score;
    }

    public void addResult(double _score, String _label){
        score = _score;
        label = _label;
    }
}
