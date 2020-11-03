package Server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) throws IOException {
        ArrayList<SocketChannel> clients = new ArrayList<SocketChannel>(); /* list of connected clients sockets */

        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(12345));

        /* Server listens for new clients connecting */
        while(true){

            /* After accepting new connections add them to clients
            and start new ServerWorker to manage each client */
            SocketChannel clientSocket = serverSocket.accept();
            clients.add(clientSocket);

            /* Start new thread worker handling the new client */
            Thread client = new Thread(new ServerWorker(clientSocket,clients));
            client.start();


            System.out.println("New Client Connected");
            System.out.println("Clients Connected: " + clients.size());

        }
    }
}