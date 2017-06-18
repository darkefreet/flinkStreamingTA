package Preprocess;

import org.codehaus.jackson.JsonNode;

/**
 * Created by wilhelmus on 15/06/17.
 */
public class JSONPathTraverse {

    public JSONPathTraverse(){

    }

    public JsonNode solve(String path, JsonNode jsonNode){
        JsonNode temp = null;
        if (path.contains(".")) {
            String[] attributes = path.split("\\.");
            temp = jsonNode.get(attributes[0]);
            if (attributes.length > 1) {
                for (int i = 1; i < attributes.length; i++) {
                    temp = temp.get(attributes[i]);
                    if (temp.equals(null)) break;
                }
            }
        } else {
            temp = jsonNode.get(path);
        }
        return temp;
    }

}
