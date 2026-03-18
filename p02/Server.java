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

    public static void main(String[] args) throws Exception {
        int port = 8080;
        int maxClients = 2;

        if (args.length >= 1) port = Integer.parseInt(args[0]);
        if (args.length >= 2) maxClients = Integer.parseInt(args[1]);
        // if (args.length < 2) System.out.println("Uso: java Server <porta> <maxClientes>");

        Selector selector = Selector.open();

        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(port));

        server.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Servidor escutando na porta: " + port + " | Tamanho da Sala: " + maxClients);

        Map<SocketChannel, ClientSession> clients = new HashMap<>();

        while (true) {
            selector.select();

            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                if (key.isAcceptable()) {
                    SocketChannel client = server.accept();

                    if (clients.size() >= maxClients) {
                        Send(client, "Sala Cheia");
                        client.close();
                        continue;
                    }
                    
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);
                    
                    ClientSession newClient = new ClientSession(client);
                    clients.put(client, newClient);
                    
                    System.out.println("Cliente conectado (" + clients.size() + "/" + maxClients + ")");
                }

                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();

                    ClientSession c = clients.get(client);
                    ByteBuffer buffer = c.buffer;

                    int bytes = client.read(buffer);

                    if (bytes == -1) {
                        clients.remove(client);
                        client.close();
                        System.out.println("Cliente desconectado (" + clients.size() + "/" + maxClients + ")");
                        Broadcast(clients, client, "** " + c.nickname + " saiu da sala **", false);
                        continue;
                    }

                    buffer.flip();

                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);

                    String message = new String(data).trim();

                    System.out.println("Messagem recebida: " + message);

                    if (c.nickname == null) {
                        c.nickname = message;

                        Send(client, "Bem-Vindo ao chat, " + c.nickname + "! Você é o Cliente #" + clients.size() + " de " + maxClients);
                        Broadcast(clients, client, "** " + c.nickname + " entrou na sala **", false);

                        buffer.clear();
                        continue;
                    }

                    if (message.equals("/usuarios")) {
                        StringBuilder users = new StringBuilder();
                        users.append("(" + clients.size() + ") Online(s):\n");
                        for (ClientSession clientConnected : clients.values()) {
                            users.append("- ").append(clientConnected.nickname).append("\n");
                        }
                        Send(client, users.toString());
                        buffer.clear();
                        continue;
                    }

                    message = "[" + c.nickname + "]: " + message;

                    Broadcast(clients, client, message, false);

                    buffer.clear();

                }
            }
        }
    }

    private static void Send(SocketChannel client, String message) throws Exception {
        client.write(ByteBuffer.wrap((message + "\n").getBytes()));
    }

    private static void Broadcast(Map<SocketChannel, ClientSession> clients, SocketChannel sender, String message, boolean includeSender) throws Exception {
        for (ClientSession client : clients.values()) {
            if (!includeSender && client.channel == sender) continue;

            Send(client.channel, message);
        }
    }

}
