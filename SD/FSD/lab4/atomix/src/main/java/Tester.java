import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Tester {
    public static void main(String[] args) throws Exception {
        ScheduledExecutorService es = Executors.newScheduledThreadPool(1);
        NettyMessagingService ms = new NettyMessagingService("nome", Address.from(12345), new MessagingConfig());

        ms.registerHandler("hello", (a,m)->{
            System.out.println("Hello "+new String(m)+" from "+a);
        }, es);

        ms.registerHandler("new", (a,m) ->{
            System.out.println("Thread number: " + new String(m) + " from " + a.port());
        }, es);

        ms.start();

        es.schedule(()-> {
            System.out.println("Timeout!");
        }, 1, TimeUnit.SECONDS);

        ms.sendAsync(Address.from("localhost", 12345), "hello", "world!".getBytes())
                .thenRun(()->{
                    System.out.println("Mensagem enviada!");
                })
                .exceptionally(t->{
                   t.printStackTrace();
                   return null;
                });

        ms.sendAsync(Address.from("localhost", 12345), "new", "12345".getBytes());
    }
}
