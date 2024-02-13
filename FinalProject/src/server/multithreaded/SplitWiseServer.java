package server.multithreaded;

import config.Config;
import logger.Logger;
import server.command.CommandCreator;
import server.command.CommandExecutor;
import server.response.Response;
import server.session.Session;
import server.session.UserSession;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SplitWiseServer {
    private final CommandExecutor commandExecutor;
    private final int port;
    private final Map<SocketChannel, Session> sessions = new HashMap<>();
    private boolean isServerWorking;
    private ByteBuffer buffer;
    private Selector selector;

    public SplitWiseServer(int port, CommandExecutor commandExecutor) {
        this.port = port;
        this.commandExecutor = commandExecutor;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(Config.DEFAULT_BUFFER_SIZE);
            isServerWorking = true;
            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    iterateSelectorKeys();

                } catch (IOException e) {
                    Logger.logError("Error occurred while processing client request", e);
                    throw new UncheckedIOException(e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            Logger.logError("Failed to start server", e);
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(Config.HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        SelectionKey clientKey = accept.register(selector, SelectionKey.OP_READ);

        Session session = new UserSession(accept);
        sessions.put(accept, session);

        clientKey.attach(selector);
    }

    private void disconnectClient(SocketChannel clientChannel) throws IOException {
        clientChannel.close();
    }

    private void iterateSelectorKeys() throws IOException {
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isReadable()) {
                SocketChannel clientChannel = (SocketChannel) key.channel();
                Session session = sessions.get(clientChannel);
                if (session == null) {
                    continue;
                }
                String clientInput = getClientInput(clientChannel);

                if (clientInput == null) {
                    continue;
                }

                Response response =
                    commandExecutor.execute(CommandCreator.newCommand(clientInput), session);
                writeClientOutput(clientChannel, response.info());
                if (clientInput.equals("disconnect")) {
                    disconnectClient(clientChannel);
                }

            } else if (key.isAcceptable()) {
                accept(selector, key);
            }
            keyIterator.remove();
        }
    }
}
