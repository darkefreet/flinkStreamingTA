package Server;

import java.io.*;
import java.net.*;

/**
 * Created by wilhelmus on 19/04/17.
 */
public class ClientDummy {
    public static void main(String[] args){
        try {
            Socket clientSocket = new Socket("localhost", 4542);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String sentence;
            while(!(sentence= inFromServer.readLine()).equals(null)) {
                System.out.println(sentence);
            }
            clientSocket.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
