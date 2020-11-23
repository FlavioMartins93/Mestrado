package Server;

import spullara.nio.channels.FutureServerSocketChannel;
import spullara.nio.channels.FutureSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class Server {
    static Broadcast broadcast = new Broadcast();

    public static void acceptConnection(FutureServerSocketChannel fssc) {
        CompletableFuture<FutureSocketChannel> sc = fssc.accept();

        sc.thenAccept(s->{
            Client c = new Client(s, broadcast);
            broadcast.addClient(c);
            System.out.println("New client connected, " + broadcast.getSize() + " clients connected.");

            acceptConnection(fssc);
            c.recRead();
            try {
                c.recWrite();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    public static void main(String[] args) throws Exception {
        FutureServerSocketChannel fssc = new FutureServerSocketChannel();
        fssc.bind(new InetSocketAddress(12345));

        acceptConnection(fssc);

        Thread.sleep(Integer.MAX_VALUE);
    }
}
