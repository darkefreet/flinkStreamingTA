package Streamprocess.WindowProcess;

import Model.Instances.Instance;
import Preprocess.DocumentsSVD;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Created by wilhelmus on 15/06/17.
 */
public interface WindowProcessInterface {

    String processData();
    void preProcessData(Instance inst);
    void setConfig(XMLConfiguration _config);

}
