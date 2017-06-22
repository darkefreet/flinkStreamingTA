package Test;

import java.io.IOException;

/**
 * Created by wilhelmus on 10/06/17.
 */
public class testClass {
    public static void main(String[] args) throws IOException {
//        DocumentsSVD d = new DocumentsSVD();
//        d.makeSVDModel("resource/news","kategori","isi");
//        System.out.println(d.search("bulutangkis","cosine"));

        String a = "test.babi.cacing";
        String s = a.substring(0,a.indexOf("."));
        String b = a.substring(a.indexOf(".")+1);
        System.out.println(b);
    }
}
