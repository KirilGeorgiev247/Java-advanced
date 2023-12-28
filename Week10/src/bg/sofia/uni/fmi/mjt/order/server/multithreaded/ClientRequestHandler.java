package bg.sofia.uni.fmi.mjt.order.server.multithreaded;

import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.repository.OrderRepository;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientRequestHandler implements Runnable {

    private static final int GET_ORDER_COMMAND_PARTS = 3;
    private final Socket socket;
    private final OrderRepository orderRepository;

    public ClientRequestHandler(Socket socket, OrderRepository orderRepository) {
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
        if (input == null || input.isBlank() || input.isEmpty()) {
            return "Unknown command";
        }

        String[] command = input.split("\\s+");
        String commandType = command[0];

        if (command[0] == null) {
            throw new IllegalArgumentException("Invalid command");
        }

        return switch (commandType) {
            case "request" -> handleRequest(command);
            case "get" -> handleGet(command);
            case "disconnect" -> "Disconnected from the server";
            default -> "Unknown command";
        };
    }

    private String handleGet(String... command) {
        if (command[1] == null) {
            throw new IllegalArgumentException("Invalid command");
        }

        if (command.length == 2) {
            return switch (command[1]) {
                case "all" -> orderRepository.getAllOrders().toString();
                case "all-successful" -> orderRepository.getAllSuccessfulOrders().toString();
                default -> "Unknown command";
            };
        }

        if (command.length == GET_ORDER_COMMAND_PARTS && command[1].equals("my-order")) {
            if (command[2] == null) {
                throw new IllegalArgumentException("Invalid command");
            }
            String[] idProp = command[2].split("=");
            if (idProp.length == 2 && idProp[0].equals("id")) {
                try {
                    int id = Integer.parseInt(idProp[1]);
                    return orderRepository.getOrderById(id).toString();
                } catch (NumberFormatException e) {
                    return "Unknown command";
                }
            }
        }

        return "Unknown command";
    }

    private String handleRequest(String... command) {
        Map<String, String> orderProps = new HashMap<>();
        for (String part : command) {
            String[] keyValuePair = part.split("=");
            if (keyValuePair.length == 2) {
                orderProps.put(keyValuePair[0].toLowerCase(), keyValuePair[1].toUpperCase());
            }
        }

        String size = orderProps.getOrDefault("size", "UNKNOWN");
        String color = orderProps.getOrDefault("color", "UNKNOWN");
        String destination = orderProps.getOrDefault("shipto", "UNKNOWN");

        size = getSize(size);
        color = getColor(color);
        destination = getDestination(destination);

        return orderRepository.request(size, color, destination).toString();
    }

    private String getSize(String size) {
        if (size.equals(Size.L.getName()) ||
            size.equals(Size.M.getName()) ||
            size.equals(Size.S.getName()) ||
            size.equals(Size.XL.getName())) {
            return size;
        }

        return "UNKNOWN";
    }

    private String getColor(String color) {
        if (color.equals(Color.RED.getName()) ||
            color.equals(Color.BLACK.getName()) ||
            color.equals(Color.WHITE.getName())) {
            return color;
        }

        return "UNKNOWN";
    }

    private String getDestination(String destination) {
        if (destination.equals(Destination.EUROPE.getName()) ||
            destination.equals(Destination.NORTH_AMERICA.getName()) ||
            destination.equals(Destination.AUSTRALIA.getName())) {
            return destination;
        }

        return "UNKNOWN";
    }
}
