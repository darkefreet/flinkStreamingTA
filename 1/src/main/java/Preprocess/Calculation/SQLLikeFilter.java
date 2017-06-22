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
            int retval = Double.compare(d1,d2);
            switch (equation) {
                case "<": {
                    ret = (retval < 0);
                    break;
                }
                case "<=": {
                    ret = (retval < 0) || (retval == 0);
                    break;
                }
                case ">=": {
                    ret = (retval > 0) || (retval == 0);
                    break;
                }
                case ">": {
                    ret = (retval > 0);
                    break;
                }
                case "==": {
                    ret = (retval == 0);
                    break;
                }
                case "!=": {
                    ret = (retval != 0);
                    break;
                }
            }
        }
        return ret;
    }

    public boolean compareNumber(double d1, double d2,String equation){
        boolean ret = false;
        int retval = Double.compare(d1,d2);
        switch (equation) {
            case "<": {
                ret = (retval < 0);
                break;
            }
            case "<=": {
                ret = (retval < 0) || (retval == 0);
                break;
            }
            case ">=": {
                ret = (retval > 0) || (retval == 0);
                break;
            }
            case ">": {
                ret = (retval > 0);
                break;
            }
            case "==": {
                ret = (retval == 0);
                break;
            }
            case "!=": {
                ret = (retval != 0);
                break;
            }
        }
        return ret;
    }


}
