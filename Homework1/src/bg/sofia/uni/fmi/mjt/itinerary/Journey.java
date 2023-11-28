package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType;

import java.math.BigDecimal;
import java.util.Objects;

public record Journey(VehicleType vehicleType, City from, City to, BigDecimal price) implements Comparable<Journey> {
    public BigDecimal getCost() {
        BigDecimal defaultPricePerKm = new BigDecimal(20);
        BigDecimal kilometerInMeters = new BigDecimal(1000);

        BigDecimal distancePrice = from.getDistanceTo(to).divide(kilometerInMeters).multiply(defaultPricePerKm);

        BigDecimal priceAfterTaxes = price.multiply(vehicleType.getGreenTax().add(new BigDecimal(1)));

        return distancePrice.add(priceAfterTaxes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCost().toString() + from() + to());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;

        if (other == null) return false;

        if (!(other instanceof Journey journey)) return false;

        return journey.vehicleType().equals(vehicleType) && journey.from().equals(from) && journey.to().equals(to) &&
            journey.price().equals(price);
    }

    @Override
    public int compareTo(Journey journey) {
        return getCost().compareTo(journey.getCost()) != 0 ? getCost().compareTo(journey.getCost()) :
            from.compareTo(journey.from()) != 0 ? from.compareTo(journey.from()) : to.compareTo(journey.to());
    }
}
