package bg.sofia.uni.fmi.mjt.order.client.multithreaded;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ShopClient {
    private static final int SERVER_PORT = 4444;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); // autoflush on
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            Thread.currentThread().setName("Shop client thread " + socket.getLocalPort());

            while (true) {
                String request = scanner.nextLine();

                if (request == null) {
                    throw new IllegalArgumentException("Invalid command!");
                }

                if ("disconnect".equals(request)) {
                    writer.println(request);
                    String reply = reader.readLine();
                    System.out.println(reply);
                    break;
                }

                writer.println(request);
                System.out.println(getResponse(reader));
            }

        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    private static String getResponse(BufferedReader reader) throws IOException {
        StringBuilder fullResponse = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("ending...")) {
                break;
            }
            fullResponse.append(line).append(System.lineSeparator());
        }

        return fullResponse.toString();
    }
}
