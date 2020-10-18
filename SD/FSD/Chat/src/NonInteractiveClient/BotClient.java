package NonInteractiveClient;

import InteractiveClient.ClientReceive;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class BotClient {

    public static void main(String[] args) throws IOException, InterruptedException {

        int sleepTime = Integer.valueOf(args[0]);

        /* Socket connects to port 12345 with IP 127.0.0.1 (localhost) */
        Socket socket = new Socket(InetAddress.getLocalHost(), 12345);

        /* out writes to socket output */
        PrintWriter out = new PrintWriter((socket.getOutputStream()));

        /* Start new client worker*/
        Thread clientWorker = new Thread(new ClientReceive(socket));
        clientWorker.start();

        while(true) {
            sleep(sleepTime*1000);
            out.println("Automatic message after sleeping: " + sleepTime + " seconds");
            out.flush();
        }
    }

}