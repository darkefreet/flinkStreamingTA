package Preprocess.Calculation

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by wilhelmus on 12/07/17.
 */
class SQLLikeFilterTest extends GroovyTestCase {

    SQLLikeFilter sql = new SQLLikeFilter();

    void testIsNotOperand() {

    }

    void testCompareNumber() {

    }

    void testCompareNumber1() {

    }

    void testParseExpression() {
        assertEquals("parser fail"," ( ( 2 + 15 * 2 ) / 6 ) OR abc != 'tftg' AND true >= 3 < 2 ! 1"
                ,sql.parseExpression("((2+15*  2) / 6) OR abc!='tftg' AND true >= 3 < 2 !1"));
    }

    void testConvertToPostfix() {
        assertEquals("convert fail","12 5 3 * 2 / + 5 12 / 6 * 1 - >= 7 9 mod 1 <= NOT AND 22 3 != OR true ! XOR "
        ,sql.convertToPostfix("(12+5*3/2) >= (5/12*6 -  1) AND NOT((7 mod 9) <= 1) OR 22 != 3 XOR !(true)"));
    }

    void testSolve() {
        ObjectMapper jsonParser = new ObjectMapper();
        JsonNode a = jsonParser.readValue("{\"op\":\"utx\",\"x\":{\"lock_time\":0,\"ver\":1,\"size\":522,\"inputs\":[{\"sequence\":4294967295,\"prev_out\":{\"spent\":true,\"tx_index\":260924851,\"type\":0,\"addr\":\"1NioRFJCatm7qrPg1uTYeLaE6ebecaQTzz\",\"value\":128355,\"n\":0,\"script\":\"76a914ee427197df3d95e5e42872bc0f9f3922dcc121cd88ac\"},\"script\":\"483045022100be21d47293db0615c91422722d2c07e3462a1378ec610e3849826c9c4055fa3702207258d2834ef3d8000073db36d09fb333aea76da711d2bb3476f3cdf6c1ba161a01210240036f4de525bbf70be1009791851b912f396138ca88c56a3ef6c54a9b49f21f\"},{\"sequence\":4294967295,\"prev_out\":{\"spent\":true,\"tx_index\":258249678,\"type\":0,\"addr\":\"1JmX552SGTYoTS9rvaymgHcJM3qhn75bXt\",\"value\":88606,\"n\":0,\"script\":\"76a914c2e58abd3cdb4b29c90cc5be306321cf3dec6de788ac\"},\"script\":\"4830450221008f5284b4ef00da006a9267c42b544e1437a391153b07f98a1ba8b49e7cc4dae0022016cc01835f2213cd1eabbc989323695ef09b0b169adec67b0178ca88508dbbad012102b65edd4d50695c14051fbdb3dd3caa401e351db9ceb5060625f0193f4d2fafce\"},{\"sequence\":4294967295,\"prev_out\":{\"spent\":true,\"tx_index\":200086733,\"type\":0,\"addr\":\"1J98WEXYtHdVdNznBHwGPcsZzm12w6QCZy\",\"value\":17321,\"n\":0,\"script\":\"76a914bc03bae5336ea66c4a21c53954fbada61d6c91bc88ac\"},\"script\":\"48304502210083253d9c6d420114eb19578820c14abc3d736ac7fe1df502e2b72d25d1e99d0902200ce93ad933d4c6bd8bf62cafc6395687f3d3fd265210dbb88c44c4f2732dfde80121032a44cdf927aa0909531f0fb588920c335829c4d9b93cbbe543ce4871ff9c0628\"}],\"time\":1497595506,\"tx_index\":260924977,\"vin_sz\":3,\"hash\":\"116fc7ac5c252f45233c5cad1f2c508e33b0c8c065e6c44c9bd13c2fd011aa0e\",\"vout_sz\":2,\"relayed_by\":\"127.0.0.1\",\"out\":[{\"spent\":false,\"tx_index\":260924977,\"type\":0,\"addr\":\"1DkpHnq3w7h6D7bAkcSdR6AQv6utkTAN8v\",\"value\":3174,\"n\":0,\"script\":\"76a9148beadefc416ee82ca0feed251813f7b7d78f923088ac\"},{\"spent\":false,\"tx_index\":260924977,\"type\":0,\"addr\":\"191FDTmfj5oeZdnFSRZ8gK17pbNnwqdfzC\",\"value\":191958,\"n\":1,\"script\":\"76a91457ccdf4aaaf9408d75c4515f10b5b8020247362888ac\"}]}}",JsonNode.class);
        JsonNode b = jsonParser.readValue("{\"created_at\":\"Fri Jun 09 04:21:56 +0000 2017\",\"id\":873032390284673024,\"id_str\":\"873032390284673024\",\"text\":\"smartfrencare: MasDab_ Maaf mas :( jika semua saran yang diberikan rekan kami sdh dilakukan dan tetap terkendala, kami sarankan utk coba la\\u2026\",\"source\":\"\\u003ca href=\\\"https:\\/\\/ifttt.com\\\" rel=\\\"nofollow\\\"\\u003eIFTTT\\u003c\\/a\\u003e\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":820512076124692481,\"id_str\":\"820512076124692481\",\"name\":\"ACEBLACK \\u2014 Mingming\",\"screen_name\":\"yaomingmign\",\"location\":null,\"url\":null,\"description\":\"Minat? DM @lockedbypyon \\ud83d\\udc99\",\"protected\":false,\"verified\":false,\"followers_count\":2,\"friends_count\":0,\"listed_count\":0,\"favourites_count\":0,\"statuses_count\":318852,\"created_at\":\"Sun Jan 15 06:04:58 +0000 2017\",\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":false,\"lang\":\"id\",\"contributors_enabled\":false,\"is_translator\":false,\"profile_background_color\":\"F5F8FA\",\"profile_background_image_url\":\"\",\"profile_background_image_url_https\":\"\",\"profile_background_tile\":false,\"profile_link_color\":\"1DA1F2\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"profile_image_url\":\"http:\\/\\/pbs.twimg.com\\/profile_images\\/820857934007545856\\/-7UWCnnb_normal.jpg\",\"profile_image_url_https\":\"https:\\/\\/pbs.twimg.com\\/profile_images\\/820857934007545856\\/-7UWCnnb_normal.jpg\",\"profile_banner_url\":\"https:\\/\\/pbs.twimg.com\\/profile_banners\\/820512076124692481\\/1484542753\",\"default_profile\":true,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"is_quote_status\":false,\"retweet_count\":0,\"favorite_count\":0,\"entities\":{\"hashtags\":[],\"urls\":[],\"user_mentions\":[],\"symbols\":[]},\"favorited\":false,\"retweeted\":false,\"filter_level\":\"low\",\"lang\":\"in\",\"timestamp_ms\":\"1496982116658\"}",JsonNode.class);
        assertEquals("1st normal comparison fail",true,sql.solve("(12+5*20)>=100 XOR false",a));
        assertEquals("2nd normal comparison fail",true,sql.solve("(1+2*(3+5))==17",a));
        assertEquals("1st json comparison fail",true,sql.solve("({x.size})==522",a));
        assertEquals("2nd json comparison fail",true,sql.solve("{user.friends_count}==0 OR {lang}==in",b));
    }
}
