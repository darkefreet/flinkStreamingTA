package DataSource;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import org.apache.flink.api.common.functions.StoppableFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Properties;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by wilhelmus on 29/10/17.
 */
public class PubnubSource extends RichSourceFunction<String> implements StoppableFunction {

    private static final Logger LOGGER = Logger.getLogger( PubnubSource.class.getName());
    private String channel;
    private Properties properties;
    private transient PubNub pubnub;
    private transient Queue<String> queue;

    public PubnubSource(Properties prop, String _channel){
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
        pubnub.destroy();
    }

    @Override
    public void run(final SourceContext<String> sourceContext) throws Exception {
//        LOGGER.log(Level.FINE,"test");
        queue = new ArrayDeque<String>();
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(properties.getProperty("appKey"));
        this.pubnub = new PubNub(pnConfiguration);
        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // call reconnect when ready
                    pubnub.reconnect();
                } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
                    // call reconnect when ready
                    pubnub.reconnect();
                } else {
                    LOGGER.log(Level.SEVERE,status.toString());
                }
            }
            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                queue.add(message.getMessage().toString());
            }
            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
            }
        });
        pubnub.subscribe().channels(Arrays.asList(channel)).execute();
        while(true){
            if(!queue.isEmpty()){
                sourceContext.collect(queue.remove());
            }
            Thread.sleep(2L);
        }
    }

    @Override
    public void cancel() {
        pubnub.destroy();
    }
}
