package InteractiveClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientReceive implements Runnable {
    private SocketChannel socket;

    public ClientReceive(SocketChannel socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ByteBuffer buf = ByteBuffer.allocate(100);

        /* Listen for messages */
        while(true) {
            try {
                this.socket.read(buf);

                buf.flip();
                System.out.print(new String(buf.array()));
                buf.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}