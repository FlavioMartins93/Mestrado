package InteractiveClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {

        /* Socket connects to port 12345 with IP 127.0.0.1 (localhost) */
        Socket socket = new Socket(InetAddress.getLocalHost(), 12345);

        /* buffer reads from System.in */
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        /* out writes to socket output */
        PrintWriter out = new PrintWriter((socket.getOutputStream()));

        /* Start new client worker*/
        Thread clientWorker = new Thread(new ClientReceive(socket));
        clientWorker.start();

        /* Read messages and send them to server */
        String temp = null;
        while(true && (temp = buffer.readLine()) != null) {
            out.println(temp);
            out.flush();
        }
    }
}