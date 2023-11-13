package bg.sofia.uni.fmi.mjt.simcity.property.buildable;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;

public class House implements Buildable, Billable {

    private BuildableType type;
    private int area;
    private double waterConsumption;
    private double electricityConsumption;
    private double naturalGasConsumption;

    public House(BuildableType type, int area,
                 double waterConsumption, double electricityConsumption, double naturalGasConsumption) {
        if (type == null || area <= 0 || waterConsumption <= 0.0 ||
            electricityConsumption <= 0.0 || naturalGasConsumption <= 0.0) {
            throw new IllegalArgumentException();
        }

        this.type = type;
        this.area = area;
        this.electricityConsumption = electricityConsumption;
        this.waterConsumption = waterConsumption;
        this.naturalGasConsumption = naturalGasConsumption;
    }

    @Override
    public double getWaterConsumption() {
        return waterConsumption;
    }

    @Override
    public double getElectricityConsumption() {
        return electricityConsumption;
    }

    @Override
    public double getNaturalGasConsumption() {
        return naturalGasConsumption;
    }

    @Override
    public BuildableType getType() {
        return type;
    }

    @Override
    public int getArea() {
        return area;
    }
}
