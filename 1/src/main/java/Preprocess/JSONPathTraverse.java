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

    public JsonNode solve(String path, JsonNode jsonNode){
        path = path.replaceAll("\\s+","");
        if(path.isEmpty() || jsonNode==null || path==null){
            return null;
        }
        JsonNode temp;
        path = path.replaceAll("\\[", ".").replaceAll("]", ".");
        if (path.charAt(path.length() - 1) == '.') {
            path = path.substring(0, path.length() - 1);
        }
        if (path.contains(".")) {
            String[] attributes = path.split("\\.");
            temp = jsonNode.get(attributes[0]);
            if(attributes.length>1) {
                for (int i = 1; i < attributes.length; i++) {
                    if (temp == null) break;
                    temp = temp.get(attributes[i]);
                }
            }
        } else {
            temp = jsonNode.get(path);
        }
        return temp;
    }

}
