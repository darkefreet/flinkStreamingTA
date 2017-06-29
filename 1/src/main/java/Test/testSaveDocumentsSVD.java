package Test;

import Preprocess.DocumentsSVD;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by wilhelmus on 28/06/17.
 */
public class testSaveDocumentsSVD {
    public static void main(String[] args) throws IOException {
        DocumentsSVD docSVD = new DocumentsSVD();
        docSVD.makeSVDModel("resource/tweetLabel","label","konten");
        FileOutputStream myFileOutputStream = new FileOutputStream("resource/svdDocuments");
        ObjectOutputStream myObjectOutputStream = new ObjectOutputStream(myFileOutputStream);
        myObjectOutputStream.writeObject(docSVD);
        myObjectOutputStream.close();
    }
}
