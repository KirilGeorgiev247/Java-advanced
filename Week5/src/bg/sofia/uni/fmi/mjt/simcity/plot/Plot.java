package bg.sofia.uni.fmi.mjt.simcity.plot;

import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableNotFoundException;
import bg.sofia.uni.fmi.mjt.simcity.exception.InsufficientPlotAreaException;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Plot<E extends Buildable> implements PlotAPI<E> {

    private int buildableArea;

    private int initialArea;

    private Map<String, E> buildings;

    public Plot(int buildableArea) {
        if (buildableArea < 0) {
            this.buildableArea = 0;
        }
        this.buildableArea = buildableArea;
        this.initialArea = buildableArea;
        buildings = new HashMap<>();
    }

    @Override
    public void construct(String address, E buildable) {
        if (address == null || address.isBlank() || address.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (buildable == null) {
            throw new IllegalArgumentException();
        }

        if (buildings.containsKey(address)) {
            throw new BuildableAlreadyExistsException();
        }

        if (buildable.getArea() > buildableArea) {
            throw new InsufficientPlotAreaException();
        }

        buildings.put(address, buildable);
        buildableArea -= buildable.getArea();
    }

    @Override
    public void demolish(String address) {
        if (address == null || address.isBlank() || address.isEmpty()) {
            throw new IllegalArgumentException();
        }

        if (buildings.isEmpty() || !buildings.containsKey(address)) {
            throw new BuildableNotFoundException();
        }

        int areaFreed = buildings.get(address).getArea();
        buildings.remove(address);
        buildableArea += areaFreed;
    }

    @Override
    public void demolishAll() {
        buildings.clear();
        buildableArea = initialArea;
    }

    @Override
    public Map<String, E> getAllBuildables() {
        Map<String, E> result = new HashMap<>();

        for (Map.Entry<String, E> entry :
            buildings.entrySet()) {
            if (entry != null && entry.getKey() != null && entry.getValue() != null) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return Collections.unmodifiableMap(result);
    }

    @Override
    public int getRemainingBuildableArea() {
        return buildableArea;
    }

    @Override
    public void constructAll(Map<String, E> buildables) {
        if (buildables == null || buildables.isEmpty()) {
            throw new IllegalArgumentException();
        }

        int areaNeeded = 0;
        boolean isValid = true;

        for (Map.Entry<String, E> entry : buildables.entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank() || entry.getKey().isEmpty() ||
                entry.getValue() == null) {
                isValid = false;
            } else {
                if (buildings.containsKey(entry.getKey())) {
                    throw new BuildableAlreadyExistsException();
                }
                areaNeeded += entry.getValue().getArea();
            }
        }

        if (areaNeeded > buildableArea) {
            throw new InsufficientPlotAreaException();
        }

        if (isValid) {
            buildings.putAll(buildables);
            buildableArea -= areaNeeded;
        }
    }
}
