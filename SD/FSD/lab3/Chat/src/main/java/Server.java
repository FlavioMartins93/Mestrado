import spullara.nio.channels.FutureServerSocketChannel;
import spullara.nio.channels.FutureSocketChannel;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Server {

    public static FutureServerSocketChannel ssc;
    public static List<CompletableFuture<FutureSocketChannel>> conList;

    public static void main(String[] args) throws Exception {
        ssc = new FutureServerSocketChannel();
        ssc.bind(new InetSocketAddress(12345));

        conList = new ArrayList<CompletableFuture<FutureSocketChannel>>();

        readLine();

        while(true) {
            sleep(999999);
        }
    }
    public static void readLine() {
        CompletableFuture<FutureSocketChannel> sc = ssc.accept();
        conList.add(sc);
        readLineRec(sc);
    }

    public static void readLineRec(CompletableFuture<FutureSocketChannel> sc){
        sc.thenAccept(s -> {
            ByteBuffer buf = ByteBuffer.allocate(1000);

            CompletableFuture<Integer> read = s.read(buf);

            read.thenAccept(i->{
                conList.forEach(s1 -> s1.thenAccept(s2 -> {
                    buf.flip();
                    s2.write(buf);
                }));
                readLineRec(sc);
            });
        }).thenCompose(r -> {
            readLine(ssc);
            return null;
        });
    }
}
