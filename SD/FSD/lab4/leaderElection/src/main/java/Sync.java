import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Sync {
    static String myPort;
    static int bestCandidate;
    static List<Integer> otherPorts;
    static ScheduledExecutorService es;
    static NettyMessagingService ms;
    static final Object lock = new Object();
    static boolean protocolStarted;

    private static void electionMessageHandle() {
        synchronized (lock) {
            ms.registerHandler("syncElection", (a,m)->{
                if(!protocolStarted) broadcastPort();
                System.out.println("Got a election message : \"" + new String(m) + "\"  from " + a);
                if(a.port() > bestCandidate) {
                    bestCandidate = a.port();
                }
            }, es);
        }
    }

    private static void broadcastPort() {
        synchronized (lock) {
            protocolStarted = true;
        }
        setTimeout();
        for(int destPort : otherPorts) {
            ms.sendAsync(Address.from("localhost", destPort), "syncElection", myPort.getBytes())
                    .thenRun(() -> {
                        System.out.println("Sent my port to " + destPort + ".");
                    })
                    .exceptionally(t -> {
                        t.printStackTrace();
                        return null;
                    });
        }
    }

    public static void setTimeout() {
        es.schedule(()-> {
            System.out.println("Timeout, " + bestCandidate + " elected leader");
        }, 10, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws Exception {
        /* args[0] contains this instance port, args[1..n] contains other ports in the election */
        myPort = args[0];
        bestCandidate = Integer.parseInt(args[0]); /* before receiving any message, this instance is the best known candidate */
        otherPorts = new ArrayList<Integer>();
        es = Executors.newScheduledThreadPool(1);
        ms = new NettyMessagingService("nome", Address.from(Integer.parseInt(myPort)), new MessagingConfig());
        protocolStarted = false;

        /* Save other ports in the election and set that their message was not received yet */
        for(int i=1; i < args.length; i++) {
            otherPorts.add(Integer.parseInt(args[i]));
        }

        ms.start().join();
        electionMessageHandle();

        System.out.println("Press any key to start the election");
        System.in.read();

        broadcastPort();
    }
}