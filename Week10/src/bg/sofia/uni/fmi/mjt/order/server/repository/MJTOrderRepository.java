package bg.sofia.uni.fmi.mjt.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

import java.util.ArrayList;
import java.util.List;

public class MJTOrderRepository implements OrderRepository {

    private List<Order> orders;
    private int idCounter;

    public MJTOrderRepository() {
        orders = new ArrayList<>();
        idCounter = 1;
    }

    private Size getValidSize(String size) {
        if (size.equals(Size.L.getName()) ||
            size.equals(Size.M.getName()) ||
            size.equals(Size.S.getName()) ||
            size.equals(Size.XL.getName())) {
            return Size.valueOf(size);
        }

        return Size.UNKNOWN;
    }

    private Color getValidColor(String color) {
        if (color.equals(Color.RED.getName()) ||
            color.equals(Color.BLACK.getName()) ||
            color.equals(Color.WHITE.getName())) {
            return Color.valueOf(color);
        }

        return Color.UNKNOWN;
    }

    private Destination getValidDestination(String destination) {
        if (destination.equals(Destination.EUROPE.getName()) ||
            destination.equals(Destination.NORTH_AMERICA.getName()) ||
            destination.equals(Destination.AUSTRALIA.getName())) {
            return Destination.valueOf(destination);
        }

        return Destination.UNKNOWN;
    }

    @Override
    public Response request(String size, String color, String destination) {
        if (size == null || color == null || destination == null ||
            size.isBlank() || color.isBlank() || destination.isBlank()) {
            throw new IllegalArgumentException("Invalid request!");
        }

        Size currOrderSize = getValidSize(size);
        Color currOrderColor = getValidColor(color);
        Destination currOrderDestination = getValidDestination(destination);

        if (currOrderColor.equals(Color.UNKNOWN) || currOrderSize.equals(Size.UNKNOWN) ||
            currOrderDestination.equals(Destination.UNKNOWN)) {
            return getInvalidResponse(currOrderSize, currOrderColor, currOrderDestination);
        }

        TShirt currTShirt = new TShirt(currOrderSize, currOrderColor);
        Order currOrder = new Order(idCounter++, currTShirt, currOrderDestination);
        orders.add(currOrder);

        return Response.create(idCounter - 1);
    }

    @Override
    public Response getOrderById(int id) {
        boolean orderFound = orders.stream().anyMatch(o -> o.id() == id);

        if (!orderFound) {
            return Response.notFound(id);
        }

        Order currOrder = orders.stream().filter(o -> o.id() == id).toList().getFirst();
        return Response.ok(List.of(currOrder));
    }

    @Override
    public Response getAllOrders() {
        return Response.ok(orders);
    }

    @Override
    public Response getAllSuccessfulOrders() {
        var succOrders = orders.stream().filter(o -> o.id() != -1).toList();
        return Response.ok(succOrders);
    }

    private Response getInvalidResponse(Size currOrderSize, Color currOrderColor, Destination currOrderDestination) {
        TShirt currTShirt = new TShirt(currOrderSize, currOrderColor);
        Order currOrder = new Order(-1, currTShirt, currOrderDestination);
        orders.add(currOrder);

        StringBuffer sb = new StringBuffer();
        sb.append("invalid=");
        if (currOrderSize.equals(Size.UNKNOWN)) {
            sb.append("size,");
        }

        if (currOrderColor.equals(Color.UNKNOWN)) {
            sb.append("color,");
        }

        if (currOrderDestination.equals(Destination.UNKNOWN)) {
            sb.append("destination");
            return Response.decline(sb.toString());
        } else {
            return Response.decline(sb.substring(0, sb.length() - 1));
        }
    }
}
