package Test;

import Algorithm.DBSCAN;
import Model.Instances.GenericInstance;
import Model.Instances.Instance;
import Preprocess.JSONPathTraverse;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by wilhelmus on 10/06/17.
 */
public class testClass {
    public static void main(String[] args) throws IOException {
        //#test 1
// DocumentsSVD d = new DocumentsSVD();
//        d.makeSVDModel("resource/news","kategori","isi");
//        System.out.println(d.search("bulutangkis","cosine"));
//

        //#test 2
//        ObjectMapper jsonParser = new ObjectMapper();
//        String json = "{\"op\":\"utx\",\"x\":{\"lock_time\":0,\"ver\":1,\"size\":522,\"inputs\":[{\"sequence\":4294967295,\"prev_out\":{\"spent\":true,\"tx_index\":260924851,\"type\":0,\"addr\":\"1NioRFJCatm7qrPg1uTYeLaE6ebecaQTzz\",\"value\":128355,\"n\":0,\"script\":\"76a914ee427197df3d95e5e42872bc0f9f3922dcc121cd88ac\"},\"script\":\"483045022100be21d47293db0615c91422722d2c07e3462a1378ec610e3849826c9c4055fa3702207258d2834ef3d8000073db36d09fb333aea76da711d2bb3476f3cdf6c1ba161a01210240036f4de525bbf70be1009791851b912f396138ca88c56a3ef6c54a9b49f21f\"},{\"sequence\":4294967295,\"prev_out\":{\"spent\":true,\"tx_index\":258249678,\"type\":0,\"addr\":\"1JmX552SGTYoTS9rvaymgHcJM3qhn75bXt\",\"value\":88606,\"n\":0,\"script\":\"76a914c2e58abd3cdb4b29c90cc5be306321cf3dec6de788ac\"},\"script\":\"4830450221008f5284b4ef00da006a9267c42b544e1437a391153b07f98a1ba8b49e7cc4dae0022016cc01835f2213cd1eabbc989323695ef09b0b169adec67b0178ca88508dbbad012102b65edd4d50695c14051fbdb3dd3caa401e351db9ceb5060625f0193f4d2fafce\"},{\"sequence\":4294967295,\"prev_out\":{\"spent\":true,\"tx_index\":200086733,\"type\":0,\"addr\":\"1J98WEXYtHdVdNznBHwGPcsZzm12w6QCZy\",\"value\":17321,\"n\":0,\"script\":\"76a914bc03bae5336ea66c4a21c53954fbada61d6c91bc88ac\"},\"script\":\"48304502210083253d9c6d420114eb19578820c14abc3d736ac7fe1df502e2b72d25d1e99d0902200ce93ad933d4c6bd8bf62cafc6395687f3d3fd265210dbb88c44c4f2732dfde80121032a44cdf927aa0909531f0fb588920c335829c4d9b93cbbe543ce4871ff9c0628\"}],\"time\":1497595506,\"tx_index\":260924977,\"vin_sz\":3,\"hash\":\"116fc7ac5c252f45233c5cad1f2c508e33b0c8c065e6c44c9bd13c2fd011aa0e\",\"vout_sz\":2,\"relayed_by\":\"127.0.0.1\",\"out\":[{\"spent\":false,\"tx_index\":260924977,\"type\":0,\"addr\":\"1DkpHnq3w7h6D7bAkcSdR6AQv6utkTAN8v\",\"value\":3174,\"n\":0,\"script\":\"76a9148beadefc416ee82ca0feed251813f7b7d78f923088ac\"},{\"spent\":false,\"tx_index\":260924977,\"type\":0,\"addr\":\"191FDTmfj5oeZdnFSRZ8gK17pbNnwqdfzC\",\"value\":191958,\"n\":1,\"script\":\"76a91457ccdf4aaaf9408d75c4515f10b5b8020247362888ac\"}]}}";
//        JsonNode a = jsonParser.readTree(json);
//        System.out.println(a.get("op").toString());
//
//        JSONPathTraverse pathSolver = new JSONPathTraverse();
//        System.out.println(pathSolver.solve("op",a));

//        //#test 3
//        String a = "http://abc.com adalah sebuah website dengan nomor telepon 08129301822 senin selasa senin 21 May 2017";
//        System.out.println(a.replaceAll("^(http|https).[\\S]*","{{url}}"));
//        System.out.println(a.replaceAll("senin","{{phone}}").replaceAll("selasa","{{phone}}"));

        //test 4
        Instance a = new Instance();
        GenericInstance b = new GenericInstance();
        a.addToNumericAttributes(15.0,1.0);
        b.addToNumericAttributes(59.395,1.0);
        Instance c = new Instance();
        c.addToNumericAttributes(1000000.0,1.0);
        ArrayList<Instance> arrInst = new ArrayList<>();
        arrInst.add(a);arrInst.add(b);arrInst.add(c);
        DBSCAN dbscanner = new DBSCAN(arrInst,5000,2);
        dbscanner.performCluster();
        ObjectMapper jsonParser = new ObjectMapper();
        System.out.println(jsonParser.writeValueAsString(dbscanner.getClusters()));
    }
}
