package NonInteractiveClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class ReadLine {

    public static String readLine(SocketChannel socket) throws IOException {

        ByteBuffer buff = ByteBuffer.allocate(1);
        StringBuilder str = new StringBuilder();


        while(socket.read(buff) == 1) {
            buff.flip();
            String charActual = StandardCharsets.UTF_8.decode(buff).toString();
            str.append(charActual);
            if(charActual.equals("\n")) return str.toString();
            buff.clear();
        }

        return null;
    }
}
