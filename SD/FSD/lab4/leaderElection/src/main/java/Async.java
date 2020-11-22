import io.atomix.cluster.messaging.MessagingConfig;
import io.atomix.cluster.messaging.impl.NettyMessagingService;
import io.atomix.utils.net.Address;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Async {
    private static String myPort;
    private static int bestCandidate;
    private static List<Integer> otherPorts;
    private static final HashMap<Integer,Boolean> messageReceived = new HashMap<Integer, Boolean>();
    private static ScheduledExecutorService es;
    private static NettyMessagingService ms;

    private static void electionMessageHandle() {
        synchronized (messageReceived) {
            ms.registerHandler("asyncElection", (a,m)->{
                System.out.println("Got a election message : \"" + new String(m) + "\"  from " + a);
                if(a.port() > bestCandidate) {
                    bestCandidate = a.port();
                }
                messageReceived.put(a.port(),true);
                if(!messageReceived.containsValue(false)) System.out.println(bestCandidate + " elected leader");
            }, es);
        }
    }

    private static void broadcastPort() {
        for(int destPort : otherPorts) {
            ms.sendAsync(Address.from("localhost", destPort), "asyncElection", myPort.getBytes())
                    .thenRun(() -> {
                        System.out.println("Sent my port to " + destPort + ".");
                    })
                    .exceptionally(t -> {
                        t.printStackTrace();
                        return null;
                    });
        }
    }

    public static void main(String[] args) throws Exception {
        /* args[0] contains this instance port, args[1..n] contains other ports in the election */
        myPort = args[0];
        bestCandidate = Integer.parseInt(args[0]); /* before receiving any message, this instance is the best known candidate */
        otherPorts = new ArrayList<Integer>();
        es = Executors.newScheduledThreadPool(1);
        ms = new NettyMessagingService("nome", Address.from(Integer.parseInt(myPort)), new MessagingConfig());

        /* Save other ports in the election and set that their message was not received yet */
        for(int i=1; i < args.length; i++) {
            otherPorts.add(Integer.parseInt(args[i]));
            synchronized (messageReceived) {
                messageReceived.put(Integer.parseInt(args[i]),false);
            }
        }

        ms.start().join();
        electionMessageHandle();

        System.out.println("Press ENTER to start the election");
        System.in.read();

        broadcastPort();
    }
}