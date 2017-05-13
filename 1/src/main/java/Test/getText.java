package Test;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by wilhelmus on 01/05/17.
 */
public class getText {
    public static void main(String[] args) throws IOException {
        String line;
        InputStream fis = new FileInputStream("twitter-indo.txt");
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        Set<String> uniqueString = new HashSet<String>();

//        PrintStream out = new PrintStream(new FileOutputStream("output.txt"));

        ObjectMapper jsonParser = new ObjectMapper();

        while ((line = br.readLine()) != null) {
            JsonNode jsonNode = jsonParser.readValue(line,JsonNode.class);
            String sentence = String.valueOf(jsonNode.get("text"));
            sentence = sentence.replaceAll("www.[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*|http.[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*", " ");
            String[] tokens = sentence.toLowerCase().split("\\W+");
            for (String token:tokens)
            {
                uniqueString.add(token);
            }
        }
        TreeSet bagOfWords = new TreeSet<String>(uniqueString);
        System.out.println(bagOfWords);

//        out.close();
    }
}
