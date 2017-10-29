package DataSource;

import org.apache.flink.api.common.functions.StoppableFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import com.satori.rtm.*;
import com.satori.rtm.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Properties;
import java.util.Queue;

/**
 * Created by wilhelmus on 15/06/17.
 */
public class SatoriSource extends RichSourceFunction<String> implements StoppableFunction {

    private Properties properties;
    static final String endpoint = "wss://open-data.api.satori.com";
    private String channel;
    private static final Logger LOG = LoggerFactory.getLogger(SatoriSource.class);

    private transient RtmClient client;
    private transient Queue<String> queue;

    public SatoriSource(Properties prop, String _channel){
        checkProperty(prop,"appKey");
        this.channel = _channel;
        this.properties = prop;
    }

    private static void checkProperty(Properties p, String key) {
        if (!p.containsKey(key)) {
            throw new IllegalArgumentException("Required property '" + key + "' not set.");
        }
    }

    @Override
    public void stop() {
        client.shutdown();
        LOG.info("RTM connection terminated");
    }

    @Override
    public void run(final SourceContext<String> scx) throws Exception {
        queue = new ArrayDeque<String>();
        client = new RtmClientBuilder(endpoint, properties.getProperty("appKey"))
            .setListener(new RtmClientAdapter() {
                @Override
                public void onEnterConnected(RtmClient client) {
                    LOG.info("Succesfully connected to RTM");
                }
            })
            .build();

        SubscriptionListener listener = new SubscriptionAdapter() {
            @Override
            public void onSubscriptionData(SubscriptionData data) {
                for(AnyJson json: data.getMessages()){
                    queue.add(json.toString());
                }
            }
        };

        client.createSubscription(channel, SubscriptionMode.SIMPLE, listener);
        client.start();

        while(true){
            if(!queue.isEmpty()){
                scx.collect(queue.remove());
            }
            Thread.sleep(2L);
        }
    }

    @Override
    public void cancel() {

    }
}
