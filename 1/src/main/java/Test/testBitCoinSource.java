package Test;

import DataSource.SatoriSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by wilhelmus on 15/06/17.
 */
public class testBitCoinSource {
    private static DataStream<String> source;
    public static void main(String[] args) throws Exception {

        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();

        // get input data
        FileInputStream input = new FileInputStream("bitcoin.properties");

        Properties prop = new Properties();
        prop.load(input);
        source = env.addSource(new SatoriSource(prop,"bitcoin-transactions"));
//        source.writeAsText("bitcoin.txt").setParallelism(1);
        source.print();
        env.execute();
    }
}
