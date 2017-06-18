package Streamprocess.WindowProcess;

import Model.Instances.Instance;
import Preprocess.DocumentsSVD;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Created by wilhelmus on 15/06/17.
 */
public interface WindowProcessInterface {

    String processData();
    void preProcessData(Instance i);
    void setConfig(XMLConfiguration _config);
    void setDocumentsSVD(DocumentsSVD doc);

}
