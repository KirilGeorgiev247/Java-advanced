package server.multithreaded;

import server.repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientRequestHandler implements Runnable {

    private final Socket socket;
    private final UserRepository orderRepository;

    public ClientRequestHandler(Socket socket, UserRepository orderRepository) {
        this.socket = socket;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("Client Request Handler for " + socket.getRemoteSocketAddress());

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = getResponse(inputLine);
                out.println(response + System.lineSeparator() + "ending...");
                if ("disconnect".equals(inputLine)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getResponse(String input) {
        if (input == null || input.isBlank()) {
            return "Unknown command";
        }

        String[] command = input.split("\\s+");
        String commandType = command[0];

        return switch (commandType) {
            // TODO: handle different command
            case "disconnect" -> "Disconnected from the server";
            default -> "Unknown command";
        };
    }
}