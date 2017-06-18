package Streamprocess;

import Model.Instances.Instance;
import Preprocess.DocumentsSVD;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.lang.reflect.Constructor;


/**
 * Created by wilhelmus on 23/05/17.
 */
public class WindowStreamProcess implements WindowFunction<Instance, String, String, TimeWindow> {

    private static transient DocumentsSVD documentsSVD;
    private static transient XMLConfiguration config;

    public WindowStreamProcess(DocumentsSVD _doc, XMLConfiguration _config) throws ConfigurationException {
        config = _config;
        documentsSVD = _doc;
    }

    @Override
    public void apply(String s, TimeWindow timeWindow, Iterable<Instance> iterable, Collector<String> collector) throws Exception {
        Class cl = Class.forName(config.getString("dataMining.processingClass"));
        Constructor con = cl.getConstructor(XMLConfiguration.class,DocumentsSVD.class);
        Object obj = con.newInstance(config,documentsSVD);

        for (Instance instance : iterable) {
            obj.getClass().getDeclaredMethod("preProcessData",instance.getClass()).invoke(obj,instance);
        }
        collector.collect(obj.getClass().getDeclaredMethod("processData").invoke(obj).toString());
    }

}
