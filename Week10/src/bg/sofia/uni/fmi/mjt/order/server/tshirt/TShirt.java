package bg.sofia.uni.fmi.mjt.order.server.tshirt;

public record TShirt(Size size, Color color) {

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("{\"size\":\"").append(size.toString())
            .append("\", \"color\":\"").append(color.toString()).append("\"}");

        return sb.toString();
    }
}
