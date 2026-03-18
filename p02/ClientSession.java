package p02;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientSession {
    public SocketChannel channel;
    public String nickname;
    public ByteBuffer buffer;

    public ClientSession(SocketChannel channel) {
        this.channel = channel;
        this.buffer = ByteBuffer.allocate(1024);
    }
}
