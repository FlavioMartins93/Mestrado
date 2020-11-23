package Server;

import java.nio.ByteBuffer;
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
            int i = 0;
            for(Client c : this.clients) i++;
            return i;
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
        synchronized (lock) {
            for(Client c : this.clients) {
                try {
                    c.putMessage(buff.duplicate());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        buff.clear();
    }
}
