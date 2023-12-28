package bg.sofia.uni.fmi.mjt.order.server.order;

import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

public record Order(int id, TShirt tShirt, Destination destination) {

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("{\"id\":").append(id)
            .append(", \"tShirt\":")
            .append(tShirt.toString())
            .append(", \"destination\":\"")
            .append(destination.toString()).append("\"}");

        return sb.toString();
    }
}
