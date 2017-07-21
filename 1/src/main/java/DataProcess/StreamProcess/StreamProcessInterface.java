package DataProcess.StreamProcess;

import Model.Instances.Instance;

import java.util.ArrayList;

/**
 * Created by wilhelmus on 20/07/17.
 */
public interface StreamProcessInterface {
    public ArrayList<Instance> transform(Instance inst);
}
