package server.session;

import java.nio.channels.SocketChannel;

public class UserSession implements Session {
    private SocketChannel channel;
    private String username; // Will be null until the user is authenticated

    public UserSession(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public SocketChannel getChannel() {
        return channel;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }
}
