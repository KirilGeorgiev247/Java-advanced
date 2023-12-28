package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.order.Order;

import java.util.Collection;

public record Response(Status status, String additionalInfo, Collection<Order> orders) {
    /**
     * Creates a response
     *
     * @param id order id
     * @return response with status Status.CREATED and with proper message for additional info
     */
    public static Response create(int id) {
        String additionalInfo = "ORDER_ID=" + id;
        return new Response(Status.CREATED, additionalInfo, null);
    }

    /**
     * Creates a response
     *
     * @param orders the orders which will be returned to the client
     * @return response with status Status.OK and Collection of orders
     */
    public static Response ok(Collection<Order> orders) {
        String additionalInfo = "";
        return new Response(Status.OK, additionalInfo, orders);
    }

    /**
     * Creates a response
     *
     * @param errorMessage the message which will be sent as additionalInfo
     * @return response with status Status.DECLINED and errorMessage as additionalInfo
     */
    public static Response decline(String errorMessage) {
        return new Response(Status.DECLINED, errorMessage, null);
    }

    /**
     * Creates a response
     *
     * @param id order id
     * @return response with status Status.NOT_FOUND and with proper message for additional info
     */
    public static Response notFound(int id) {
        String additionalInfo = "Order with id = " + id + " does not exist.";
        return new Response(Status.NOT_FOUND, additionalInfo, null);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        return sb.append("{").append("\"status\"").append(":").append("\"").append(status.toString()).append("\"")
            .append(getAdditionalInfo()).append(getOrdersInfo()).append("}").toString();
    }

    private String getAdditionalInfo() {
        StringBuffer sb = new StringBuffer();

        if (additionalInfo != null && !additionalInfo.isBlank() && !additionalInfo.isEmpty()) {
            sb.append(", \"additionalInfo\"").append(":").append("\"").append(additionalInfo).append("\"");
        }

        return sb.toString();
    }

    private String getOrdersInfo() {
        StringBuffer sb = new StringBuffer();

        if (orders != null && !orders.isEmpty()) {
            sb.append(", \"orders\":[");
            orders.forEach(o -> sb.append(o.toString()).append(",").append(System.lineSeparator()));

            int newlineLength = System.lineSeparator().length();
            if (sb.length() > newlineLength) {
                sb.delete(sb.length() - newlineLength - 1, sb.length());
            }

            sb.append("]");
        }

        return sb.toString();
    }

    private enum Status {
        OK, CREATED, DECLINED, NOT_FOUND
    }
}