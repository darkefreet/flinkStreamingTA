package Test;

import Preprocess.DocumentsSVD;

import java.io.IOException;

/**
 * Created by wilhelmus on 10/06/17.
 */
public class testClass {
    public static void main(String[] args) throws IOException {
        DocumentsSVD d = new DocumentsSVD("resource/news","kategori","isi");
        d.search("bulutangkis");
    }
}
