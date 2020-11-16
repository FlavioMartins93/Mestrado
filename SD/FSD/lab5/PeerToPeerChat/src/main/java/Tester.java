import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Tester {
    public static void main(String[] args) throws Exception {
        ScheduledExecutorService es = Executors.newScheduledThreadPool(1);
        NettyMessagingService ms = new NettyMessagingService("p2pChat", Address.from("localhost", Integer.parseInt(args[0])), new MessagingConfig());

        ms.registerHandler("message", (a,m) ->{
            System.out.println("Message: " + new String(m) + " from " + a.port());
        }, es);

        ms.start();

        // Socket


        ByteBuffer buf = ByteBuffer.allocate(100);
        /* Read messages and send them to server */
        ReadableByteChannel channel = Channels.newChannel(System.in);
        while (channel.read(buf) >= 0) {
            for(int i=0;i<args.length;i++) {
                buf.flip();
                ms.sendAsync(Address.from("localhost", Integer.parseInt(args[i])), "message", buf.array());
                buf.clear();
            }
        }
    }
}
