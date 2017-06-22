package Preprocess;

import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonNode;

/**
 * Created by wilhelmus on 15/06/17.
 */
public class JSONPathTraverse {

    private NumberUtils numUtils;

    public JSONPathTraverse(){
        numUtils = new NumberUtils();
    }

    public JsonNode solve(String path, JsonNode jsonNode){
        if(path==null || jsonNode==null || path==""){
            return null;
        }
        JsonNode temp;
        path = path.replaceAll("\\[", ".").replaceAll("\\]", ".");
        if (path.charAt(path.length() - 1) == '.') {
            path = path.substring(0, path.length() - 1);
        }

        if (path.contains(".")) {
            String[] attributes = path.split("\\.");
            temp = jsonNode.get(attributes[0]);
            if(attributes.length>1) {
                for (int i = 1; i < attributes.length; i++) {
                    if(temp!=null)
                        temp = temp.get(attributes[i]);
                    if (temp == null) break;
                }
            }
        } else {
            temp = jsonNode.get(path);
        }
        return temp;
    }

    public double total(String path, JsonNode jsonNode){
        double ret = 0.0;
        if(jsonNode!=null && path !=null && path!="") {
            JsonNode temp;
            path = path.replaceAll("\\[", ".").replaceAll("\\]", ".");
            if (path.charAt(path.length() - 1) == '.') {
                path = path.substring(0, path.length() - 1);
            }
            //traverse through the attributes
            if (path.contains(".")) {
                temp = jsonNode.get(path.substring(0, path.indexOf(".")));
                path = path.substring(path.indexOf(".") + 1);
                if (temp.isArray()) {
                    if (numUtils.isNumber(path.substring(0, path.indexOf("."))) && temp != null) {
                        temp = jsonNode.get(path.substring(0, path.indexOf(".")));
                        path = path.substring(path.indexOf(".") + 1);
                        ret += total(path, temp);
                    } else {
                        for (JsonNode j : temp) {
                            ret += total(path, j);
                        }
                    }
                } else {
                    ret += total(path, temp);
                }

            } else if (jsonNode.isArray()) {
                for (JsonNode j : jsonNode) {
                    if (jsonNode.isInt() || jsonNode.isDouble()) {
                        ret += jsonNode.getDoubleValue();
                    }
                }
            } else {
                if (jsonNode.isInt() || jsonNode.isDouble()) {
                    ret += jsonNode.getDoubleValue();
                }
            }
        }
        return ret;
    }

}
