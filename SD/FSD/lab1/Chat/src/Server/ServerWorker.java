package Server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class ServerWorker implements Runnable{
    private SocketChannel socket;
    private ArrayList<SocketChannel> clients;

    public ServerWorker(SocketChannel socket, ArrayList<SocketChannel> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            ByteBuffer buf = ByteBuffer.allocate(100);

            /* Listen for clients messages */
            while(true) {
                this.socket.read(buf);
                buf.flip();

                /* Broadcast client message to all connected clients */
                for (SocketChannel sc : clients) {
                    sc.write(buf.duplicate());
                }
                buf.clear();
            }
        } catch(IOException e) {
            /* Remove this client from the list of connected clients */
            this.clients.remove(this.socket);
        }
    }
}