package server.session;

import java.nio.channels.SocketChannel;

public interface Session {
    public SocketChannel getChannel();

    public String getUsername();

    public void setUsername(String username);
}
