package Test;

import Preprocess.DocumentsSVD;

import java.io.IOException;

/**
 * Created by wilhelmus on 10/06/17.
 */
public class testClass {
    public static void main(String[] args) throws IOException {
        DocumentsSVD d = new DocumentsSVD();
        d.makeSVDModel("resource/news","kategori","isi");
        System.out.println(d.search("bulutangkis","cosine"));
    }
}
