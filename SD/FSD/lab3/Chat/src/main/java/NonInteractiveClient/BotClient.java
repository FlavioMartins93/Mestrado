package NonInteractiveClient;

import InteractiveClient.ClientReceive;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

public class BotClient {

    /**
     * The nonInteractiveClient(BotClient) implements a bot that sleeps a given time between sending or printing received messages.
     * args[0] = port to connect
     * args[1] = sleep time in ms between sending messages
     * args[2] = sleep time in ms between printing received messages
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        int sleepTime = Integer.parseInt(args[1]);

        /* Socket connects to port in args[0] with IP 127.0.0.1 (localhost) */
        SocketChannel socket = SocketChannel.open();
        socket.connect(new InetSocketAddress(InetAddress.getLocalHost(),Integer.parseInt(args[0])));

        /*  Start new client Receiver and Writer
            The receiver will add incoming messages to the queue and notify the writer
            The writer will sleep 10 seconds and then wait for notification to write some message
        */
        Thread clientReceiver = new Thread(new BotClientReceive(socket, Integer.parseInt(args[2])));
        clientReceiver.start();

        ByteBuffer buf = ByteBuffer.allocate(100);
        /* Send them to server */
        buf.put(("Message sent after sleeping: " + sleepTime + "\n").getBytes());
        buf.flip();
        while (true) {
            Thread.sleep(sleepTime);
            socket.write(buf.duplicate());
        }
    }

}