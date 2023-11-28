package bg.sofia.uni.fmi.mjt.itinerary;

import java.math.BigDecimal;
import java.util.Objects;

public record City(String name, Location location) implements Comparable<City> {
    public BigDecimal getDistanceTo(City other) {
        double latitudeDiff = Math.abs(location.x() - other.location().x());
        double longitudeDiff = Math.abs(location.y() - other.location().y());
        return BigDecimal.valueOf(latitudeDiff + longitudeDiff);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public int compareTo(City city) {
        return name.compareTo(city.name());
    }
}
