package Streamprocess;

import Algorithm.DBSCAN;
import Model.AnalysisResult;
import Model.Instance;
import org.apache.flink.hadoop.shaded.com.google.common.collect.Iterables;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * Created by wilhelmus on 23/05/17.
 */
public class WindowStreamProcess implements WindowFunction<Instance, AnalysisResult, String, TimeWindow> {
    @Override
    public void apply(String s, TimeWindow timeWindow, Iterable<Instance> iterable, Collector<AnalysisResult> collector) throws Exception {
//        AnalysisResult result = new AnalysisResult();
//
//        if(Iterables.size(iterable)>0) {
//            for (Instance i : iterable) {
//                result.addInstance(i);
//            }
//            result.calculateSVD();
//            DBSCAN dbscan = new DBSCAN(result.getInstances(),0.4f,2);
//            dbscan.performCluster();
//            result.setClusters(dbscan.getClusters());
//        }
        collector.collect(null);
    }
}