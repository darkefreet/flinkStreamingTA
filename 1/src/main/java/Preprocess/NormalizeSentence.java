package Preprocess;

import IndonesianNLP.IndonesianSentenceFormalization;
import IndonesianNLP.IndonesianStemmer;

/**
 * Created by wilhelmus on 17/05/17.
 */
public class NormalizeSentence {
    private String sentence;

    public NormalizeSentence(String _sentence){
        _sentence = _sentence.replaceAll("^(http|https).[\\S]*","LNKURL").replaceAll("08.[\\S]*","PHNNUM")
        .replaceAll("[\\S]\\d.[Rb|Jt|rb|jt]","CRRNCY").replaceAll("[R][p][.]\\d\\d[\\S]","CRRNCY").replaceAll("[R][p.]\\d\\d[\\S]","CRRNCY")
        .replaceAll("-"," ").replaceAll("(?:\\(|\\)|\\]|\\[|\\{|\\}|,|\\.)"," ").replaceAll("(?:[\\S].%|[\\S]%)","PRCNTG").toLowerCase();
        IndonesianSentenceFormalization formalizer = new IndonesianSentenceFormalization();
        formalizer.initStopword();
        sentence = formalizer.normalizeSentence(_sentence);
        sentence = formalizer.deleteStopword(sentence);
        IndonesianStemmer stemmer = new IndonesianStemmer();
        sentence = stemmer.stemSentence(sentence);
        sentence.replaceAll("(?:senin|selasa|rabu|kamis|jumat|sabtu|minggu|januari|februari|maret|april|mei|juni|juli|agustus|september|oktober|november|desember|\\d\\d\\d\\d)","TMMRKR");
    }

    public String getSentence(){
        return sentence;
    }
}
