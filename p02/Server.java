package p02;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server {
    private static final int PORT = 8080;
    private static final int MAX_CLIENTS = 2;

    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();

        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(PORT));

        server.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Servidor escutando na porta " + PORT);

        Map<SocketChannel, ByteBuffer> clients = new HashMap<>();

        while (true) {
            selector.select();

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isAcceptable()) {
                    SocketChannel client = server.accept();

                    if (clients.size() >= MAX_CLIENTS) {
                        client.write(ByteBuffer.wrap("Sala cheia\n".getBytes()));
                        client.close();
                        continue;
                    }

                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);

                    clients.put(client, ByteBuffer.allocate(1024));

                    System.out.println("Cliente conectado (" + clients.size() + "/" + MAX_CLIENTS + ")");
                }

                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = clients.get(client);

                    int bytes = client.read(buffer);

                    if (bytes == -1) {
                        clients.remove(client);
                        client.close();
                        System.out.println("Cliente desconectado (" + clients.size() + "/" + MAX_CLIENTS + ")");
                        continue;
                    }

                    buffer.flip();

                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);

                    String message = new String(data).trim();

                    System.out.println("Messagem recebida: " + message);

                    for (SocketChannel other : clients.keySet()) 
                        if (other != client) other.write(ByteBuffer.wrap((message + "\n").getBytes()));

                    buffer.clear();

                }
            }
        }
    }
}
