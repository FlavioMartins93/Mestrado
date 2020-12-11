package NonInteractiveClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class BotClientReceive implements Runnable {
    private SocketChannel socket;
    private int sleepTime;

    public BotClientReceive(SocketChannel socket, int sleepTime) {
        this.socket = socket;
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        ByteBuffer buf = ByteBuffer.allocate(100);

        /* Listen for messages */
        while(true) {
            try {
                Thread.sleep(this.sleepTime);

                //this.socket.read(buf);

                buf.flip();
                String msg = ReadLine.readLine(this.socket);
                if(msg != null) System.out.print("After sleeping: "+ this.sleepTime + " | Received: " + msg);
                buf.clear();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}