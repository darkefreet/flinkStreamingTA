package Test;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by wilhelmus on 28/10/17.
 */
public class PubNubTest {

    private static final Logger LOGGER = Logger.getLogger( PubNubTest.class.getName());
    public static void main(String[] args){
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey("e19f2bb0-623a-11df-98a1-fbd39d75aa3f");
        PubNub pubnub = new PubNub(pnConfiguration);
        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {
                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // internet got lost, do some magic and call reconnect when ready
                    pubnub.reconnect();
                } else if (status.getCategory() == PNStatusCategory.PNTimeoutCategory) {
                    // do some magic and call reconnect when ready
                    pubnub.reconnect();
                } else {
                    LOGGER.log(Level.SEVERE,status.toString());
                }
            }
            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                System.out.println("message : " + message.getMessage());
            }
            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                System.out.println(presence.getJoin());
            }
        });
        pubnub.subscribe().channels(Arrays.asList("rts-xNjiKP4Bg4jgElhhn9v9-geo-map")).execute();
    }
}
