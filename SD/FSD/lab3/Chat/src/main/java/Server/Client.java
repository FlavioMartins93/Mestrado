package Server;

import spullara.nio.channels.FutureSocketChannel;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {

    FutureSocketChannel s;
    Broadcast broadcast;
    BlockingQueue messages;

    public Client(FutureSocketChannel s, Broadcast broadcast) {
        this.s = s;
        this.broadcast = broadcast;
        this.messages = new LinkedBlockingQueue();
    }

    public void recWrite() throws InterruptedException {
        ByteBuffer buff = (ByteBuffer) this.messages.take();
        this.s.write(buff).thenAccept(m -> {
            try {
                recWrite();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        buff.clear();
    }

    public void putMessage(ByteBuffer buff) throws InterruptedException {
        this.messages.put(buff);
    }

    public void recRead() {
        ByteBuffer buff = ByteBuffer.allocate(1000);

        this.s.read(buff).thenAccept( message -> {
            if( message == null) this.broadcast.removeClient(this);
            buff.flip();
            this.broadcast.broadcast(buff);
            recRead();
        });
    }
}
