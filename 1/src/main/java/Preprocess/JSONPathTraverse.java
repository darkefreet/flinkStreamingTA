package Preprocess;


import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by wilhelmus on 15/06/17.
 */
public class JSONPathTraverse {

    private static ObjectMapper jsonParser;

    public JSONPathTraverse(){
        if(jsonParser==null){
            jsonParser = new ObjectMapper();
        }
    }

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    public JsonNode solve(String path, JsonNode jsonNode){
        path = path.replaceAll("\\s+","");
        if(path.isEmpty() || jsonNode==null || path==null){
            return null;
        }
        JsonNode temp;
        path = path.replaceAll("\\[", ".").replaceAll("]", "");
        if (path.charAt(path.length() - 1) == '.') {
            path = path.substring(0, path.length() - 1);
        }
        if (path.contains(".")) {
            String[] attributes = path.split("\\.");
            temp = jsonNode.get(attributes[0]);
            if(attributes.length>1) {
                for (int i = 1; i < attributes.length; i++) {
                    if (temp == null) break;
                    if(temp.isArray() && isInteger(attributes[i]))
                        temp = temp.get(Integer.parseInt(attributes[i]));
                    else
                        temp = temp.get(attributes[i]);
                }
            }
        } else {
            temp = jsonNode.get(path);
        }
        return temp;
    }

}
