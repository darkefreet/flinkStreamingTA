package Server;
/**
 * Created by wilhelmus on 19/04/17.
 */

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ServerDummy implements Runnable{
    private Socket cSocket;
    public ServerDummy(Socket _socket){
        this.cSocket = _socket;
    }
    public static void main(String[] args)throws Exception{

        ServerSocket dummySocket = new ServerSocket(4542);
        while(true){
            Socket connection= dummySocket.accept();
            System.out.println("connected");
            new Thread(new ServerDummy(connection)).start();
        }
    }
    public void run() {
        try {
            PrintStream pstream = new PrintStream(cSocket.getOutputStream());
            String line;
            try (
                    InputStream fis = new FileInputStream("twitter-indo.txt");
                    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
                    BufferedReader br = new BufferedReader(isr);
            ) {
                while ((line = br.readLine()) != null) {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 9000 + 1));
                    System.out.println(line);
                    pstream.println(line);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            pstream.close();
            cSocket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
