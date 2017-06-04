package Server;

import java.io.*;
import java.net.*;

/**
 * Created by wilhelmus on 19/04/17.
 */
public class ClientDummy {
    public static void main(String[] args){
        try {
            ServerSocket clientSocket = new ServerSocket(4543);
            while(true){
                Socket connection= clientSocket.accept();
                System.out.println("connected");
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String sentence;
                while(!(sentence= inFromServer.readLine()).equals(null)) {
                    System.out.println(sentence);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
