package Server;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Broadcast {
    List<Client> clients;
    Object lock;

    public Broadcast() {
        this.clients = new ArrayList<>();
        this.lock = new Object();
    }

    public int getSize() {
        synchronized (lock) {
            return this.clients.size();
        }
    }

    public void addClient(Client c) {
        synchronized (lock) {
            this.clients.add(c);
        }
    }

    public void removeClient(Client c) {
        synchronized (lock) {
            this.clients.remove(c);
            System.out.println("1 client left");
        }
    }

    public void broadcast(ByteBuffer buff) {
        String msg = StandardCharsets.UTF_8.decode(buff).toString();
        buff.clear();
        synchronized (lock) {
            for(Client c : this.clients) {
                try {
                    c.putMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
