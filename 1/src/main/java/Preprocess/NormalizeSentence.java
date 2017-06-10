package Preprocess;

import IndonesianNLP.IndonesianSentenceFormalization;
import IndonesianNLP.IndonesianStemmer;

/**
 * Created by wilhelmus on 17/05/17.
 */
public class NormalizeSentence {
    private String sentence;

    public NormalizeSentence(String _sentence){
        IndonesianSentenceFormalization formalizer = new IndonesianSentenceFormalization();
        formalizer.initStopword();
        sentence = formalizer.normalizeSentence(_sentence);
        sentence = formalizer.deleteStopword(sentence);
//        IndonesianStemmer stemmer = new IndonesianStemmer();
//        sentence = stemmer.stemSentence(sentence);
    }

    public String getSentence(){
        return sentence;
    }
}
