package bg.sofia.uni.fmi.mjt.simcity.utility;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UtilityService implements UtilityServiceAPI {

    private final int roundHelper = 100;
    private Map<UtilityType, Double> taxRates;

    public UtilityService(Map<UtilityType, Double> taxRates) {
        if (taxRates == null || taxRates.isEmpty()) {
            this.taxRates = new HashMap<>();
        } else {
            this.taxRates = taxRates;
        }
    }

    @Override
    public <T extends Billable> double getUtilityCosts(UtilityType utilityType, T billable) {
        if (utilityType == null || billable == null) {
            throw new IllegalArgumentException();
        }

        if (taxRates.isEmpty()) {
            return 0.0;
        }

        return switch (utilityType) {
            case WATER -> billable.getWaterConsumption() * taxRates.get(UtilityType.WATER);
            case ELECTRICITY -> billable.getElectricityConsumption() * taxRates.get(UtilityType.ELECTRICITY);
            case NATURAL_GAS -> billable.getNaturalGasConsumption() * taxRates.get(UtilityType.NATURAL_GAS);
        };
    }

    @Override
    public <T extends Billable> double getTotalUtilityCosts(T billable) {
        if (billable == null) {
            throw new IllegalArgumentException();
        }

        double sum = billable.getElectricityConsumption() * taxRates.get(UtilityType.ELECTRICITY) +
            billable.getWaterConsumption() * taxRates.get(UtilityType.WATER) +
            billable.getNaturalGasConsumption() * taxRates.get(UtilityType.NATURAL_GAS);

        return sum;
    }

    @Override
    public <T extends Billable> Map<UtilityType, Double> computeCostsDifference(T firstBillable, T secondBillable) {
        if (firstBillable == null || secondBillable == null) {
            throw new IllegalArgumentException();
        }

        double waterDiff = Math.abs(
            getUtilityCosts(UtilityType.WATER, firstBillable) - getUtilityCosts(UtilityType.WATER, secondBillable));

        double electricityDiff = Math.abs(getUtilityCosts(UtilityType.ELECTRICITY, firstBillable) -
            getUtilityCosts(UtilityType.ELECTRICITY, secondBillable));

        double naturalGasDiff = Math.abs(getUtilityCosts(UtilityType.NATURAL_GAS, firstBillable) -
            getUtilityCosts(UtilityType.NATURAL_GAS, secondBillable));

        return Collections.unmodifiableMap(
            Map.of(UtilityType.WATER, waterDiff, UtilityType.ELECTRICITY, electricityDiff, UtilityType.NATURAL_GAS,
                naturalGasDiff));
    }
}
