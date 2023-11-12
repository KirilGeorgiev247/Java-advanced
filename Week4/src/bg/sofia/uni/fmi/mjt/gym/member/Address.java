package bg.sofia.uni.fmi.mjt.gym.member;

public record Address(double longitude, double latitude) {
    public double getDistanceTo(Address other) {
        return Math.sqrt(
            (longitude - other.longitude()) * (longitude - other.longitude()) +
                (latitude - other.latitude()) * (latitude - other.latitude()));
    }
}
