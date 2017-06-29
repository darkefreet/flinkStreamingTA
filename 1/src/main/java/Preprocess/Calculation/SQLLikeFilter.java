package Preprocess.Calculation;

import org.apache.commons.lang.math.NumberUtils;

/**
 * Created by wilhelmus on 21/06/17.
 */
public class SQLLikeFilter {

    private NumberUtils utils;

    public SQLLikeFilter(){
        utils = new NumberUtils();
    }

    public boolean compareNumber(String num1,String num2,String equation){
        boolean ret = false;
        if(utils.isNumber(num1)&&utils.isNumber(num2)) {
            Double d1 = Double.valueOf(num1);
            Double d2 = Double.valueOf(num2);
            switch (equation) {
                case "<": {
                    ret = d1 < d2;
                    break;
                }
                case "<=": {
                    ret = d1 <= d2;
                    break;
                }
                case ">=": {
                    ret = d1>=d2;
                    break;
                }
                case ">": {
                    ret = d1>d2;
                    break;
                }
                case "==": {
                    ret = d1==d2;
                    break;
                }
                case "!=": {
                    ret = d1!=d2;
                    break;
                }
            }
        }
        return ret;
    }

    public boolean compareNumber(double d1, double d2,String equation){
        boolean ret = false;
        switch (equation) {
            case "<": {
                ret = d1 < d2;
                break;
            }
            case "<=": {
                ret = d1 <= d2;
                break;
            }
            case ">=": {
                ret = d1>=d2;
                break;
            }
            case ">": {
                ret = d1>d2;
                break;
            }
            case "==": {
                ret = d1==d2;
                break;
            }
            case "!=": {
                ret = d1!=d2;
                break;
            }
        }
        return ret;
    }


}
