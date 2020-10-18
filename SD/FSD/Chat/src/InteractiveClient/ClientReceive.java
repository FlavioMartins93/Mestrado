package InteractiveClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReceive implements Runnable {
    Socket socket;

    public ClientReceive(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            /* In reads from socket input steam */
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            /* Each time a message from server is received, print it */
            while(true) {
                try {
                    System.out.println(in.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}