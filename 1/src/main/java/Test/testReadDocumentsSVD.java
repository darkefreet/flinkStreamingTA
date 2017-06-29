package Test;

import Preprocess.DocumentsSVD;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by wilhelmus on 28/06/17.
 */
public class testReadDocumentsSVD {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        FileInputStream myFileInputStream = new FileInputStream("resource/svdDocuments");
        ObjectInputStream myObjectInputStream = new ObjectInputStream(myFileInputStream);
        DocumentsSVD docSVD = (DocumentsSVD) myObjectInputStream.readObject();
        myObjectInputStream.close();
        if(docSVD.doesHasModel()){
            System.out.println("test");
        }
        System.out.println(docSVD.classify("jangan asal bicara kamu","dot","promo"));
    }
}
