package client.multithreaded;

import config.Config;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class SplitWiseClient implements Closeable {
    private SocketChannel socketChannel;
    private ByteBuffer buffer = ByteBuffer.allocate(Config.DEFAULT_BUFFER_SIZE);

    public SplitWiseClient(String host, int port) throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
    }

    public static void main(String[] args) {
        try (SplitWiseClient client = new SplitWiseClient(Config.HOST, Config.PORT);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("Enter command: ");
                String input = consoleReader.readLine();

                try {
                    client.sendMessage(input);

                    String response = client.receiveMessage();
                    System.out.println(response);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    break;
                }

                if (input.equals("disconnect")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(
                "Unable to connect to the server. Try again later or contact administrator by providing the logs in " +
                    Config.LOG_FILE);
        }
    }

    public void sendMessage(String message) throws IOException {
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
    }

    public String receiveMessage() throws IOException {
        buffer.clear();
        int readBytes = socketChannel.read(buffer);
        if (readBytes == -1) {
            throw new IOException("Server has closed the connection");
        }
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public void close() throws IOException {
        socketChannel.close();
    }
}
