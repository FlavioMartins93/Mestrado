package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static ArrayList<Socket> clients;       /* list of connected clients sockets */

    public static void main(String[] args) throws IOException {
        clients = new ArrayList<Socket>();

        ServerSocket serverSocket = new ServerSocket(12345);
        /* Server listens for new clients connecting */
        while(true){

            /* After accepting new connections add them to clients
            and start new ServerWorker to manage each client */
            Socket clientSocket = serverSocket.accept();
            clients.add(clientSocket);

            Thread client = new Thread(new ServerWorker(clientSocket,clients));
            client.start();

            /*
            System.out.println("New Client Connected");
            System.out.println("Clients Connected: " + clients.size());
            */
        }
    }
}