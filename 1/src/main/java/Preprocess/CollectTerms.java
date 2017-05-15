package Preprocess;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by wilhelmus on 15/05/17.
 */
public class CollectTerms {
    public static void main(String[] args) throws Exception{
        Set<String> terms = new HashSet<String>();
        Set<String> stopWords = new HashSet<String>();
        String line;
        NumberUtils numUtils = new NumberUtils();
        InputStream fis = new FileInputStream("resources/stopwords");
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);
        while ((line = br.readLine()) != null) {
            stopWords.add(line);
        }

        File folder = new File("resources/news/");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                System.out.println(file.getName());
                String content = FileUtils.readFileToString(file);
                content.replaceAll("[0-9]", " ");
                String[] tokens = content.toLowerCase().split("\\W+");
                for(String token : tokens){
                    if (!terms.contains(token) && token.length()>1 && !numUtils.isNumber(token) &&!stopWords.contains(token)){
                        terms.add(token);
                    }
                }
            }
        }

        List<String> sortedList = new ArrayList(terms);
        Collections.sort(sortedList);

        PrintWriter outputFile = new PrintWriter(new FileWriter("resources/terms.txt"));

        for (String term : sortedList){
            outputFile.println(term);
        }
        outputFile.close();
    }
}
