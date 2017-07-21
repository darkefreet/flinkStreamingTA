package DataProcess;

import Model.Instances.Instance;
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

    private static transient XMLConfiguration config;

    public WindowStreamProcess( XMLConfiguration _config) throws ConfigurationException {
        config = _config;
    }

    @Override
    public void apply(String s, TimeWindow timeWindow, Iterable<Instance> iterable, Collector<String> collector) throws Exception {
        Class cl = Class.forName(config.getString("window.processingClass"));
        Constructor con = cl.getConstructor();
        Object obj = con.newInstance();
        for (Instance instance : iterable) {
            obj.getClass().getDeclaredMethod("preProcessData",instance.getClass()).invoke(obj,instance);
        }
        String ret = String.class.cast(obj.getClass().getDeclaredMethod("processData").invoke(obj));
        collector.collect(ret);
    }

}
