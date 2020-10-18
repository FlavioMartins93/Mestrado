package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerWorker implements Runnable{
    private Socket socket;
    ArrayList<Socket> clients;

    public ServerWorker(Socket socket, ArrayList<Socket> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter((socket.getOutputStream()));
            String temp = null;

            /* Listen for clients messages */
            while (true && (temp = input.readLine())!= null) {
                System.out.println(temp);

                /* Broadcast client message to all connected clients */
                for(Socket sock : clients) {
                    PrintWriter outAux = new PrintWriter((sock.getOutputStream()));
                    outAux.println(temp);
                    outAux.flush();
                }
            }
        } catch(IOException e){
            //e.printStackTrace();
            /* When an IOException is catch remove the client from the clients list and close the socket */
            this.clients.remove(this.socket);
            try {
                this.socket.shutdownOutput();                /* Close socket write side */
                this.socket.shutdownInput();                 /* Close socket read side */
                this.socket.close();                         /* Close socket */
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}