package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.InvalidJourneyException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SequencedCollection;
import java.util.Set;

public class RideRight implements ItineraryPlanner {
    private final List<Journey> schedule;
    private final Map<City, BigDecimal> pathCosts;
    private final Map<City, Journey> bestPath;
    private final Set<City> cities;
    private final Queue<City> potentialStops;

    public RideRight(List<Journey> schedule) {
        validateJourneys(schedule);

        this.schedule = schedule;
        pathCosts = new HashMap<>();
        bestPath = new HashMap<>();
        cities = new HashSet<>();
        potentialStops = new PriorityQueue<>(new CityComparator());

        for (Journey journey : schedule) {
            cities.add(journey.from());
            cities.add(journey.to());
        }
    }

    @Override
    public SequencedCollection<Journey> findCheapestPath(City start, City destination, boolean allowTransfer)
        throws CityNotKnownException, NoPathToDestinationException {
        if (!cities.contains(start)) {
            throw new CityNotKnownException("Start city is not present in the list of provides journeys!");
        }

        if (!cities.contains(destination)) {
            throw new CityNotKnownException("Destination city is not present in the list of provides journeys!");
        }

        if (allowTransfer) {
            setCheapestPathWithTransfers(start, destination);
            return getPathResult(destination);
        } else {
            return getCheapestJourneyBetweenTwo(start, destination);
        }
    }

    private List<Journey> getPathResult(City destination) {
        List<Journey> path = new LinkedList<>();
        City cityIterator = destination;
        while (cityIterator != null && bestPath.containsKey(cityIterator)) {
            Journey currentJourney = bestPath.get(cityIterator);
            if (currentJourney != null) {
                path.addFirst(currentJourney);
            }
            cityIterator = currentJourney.from();
        }

        return path;
    }

    private void setCheapestPathWithTransfers(City start, City destination) throws NoPathToDestinationException {
        potentialStops.add(start);
        pathCosts.put(start, BigDecimal.ZERO);

        while (!potentialStops.isEmpty()) {
            City currentCity = potentialStops.poll();
            if (currentCity.equals(destination)) {
                return;
            }

            var journeys = getJourneysFrom(currentCity);
            if (journeys.isEmpty()) {
                continue;
            }

            for (Journey journey : journeys) {
                City neighbor = journey.to();
                BigDecimal currentJourneyCost =
                    pathCosts.getOrDefault(currentCity, new BigDecimal(Double.MAX_VALUE)).add(journey.getCost());

                if (currentJourneyCost.compareTo(pathCosts.getOrDefault(neighbor, new BigDecimal(Double.MAX_VALUE))) <
                    0) {
                    bestPath.put(neighbor, journey);
                    pathCosts.put(neighbor, currentJourneyCost);
                    if (!potentialStops.contains(neighbor)) {
                        potentialStops.add(neighbor);
                    }
                }
            }
        }

        throw new NoPathToDestinationException("There is no path satisfying the conditions!");
    }

    private List<Journey> getJourneysFrom(City city) {
        List<Journey> journeysFromCity = new ArrayList<>();

        for (Journey journey : schedule) {
            if (journey.from().equals(city)) {
                journeysFromCity.add(journey);
            }
        }

        return journeysFromCity;
    }

    private List<Journey> getCheapestJourneyBetweenTwo(City start, City destination) {
        Queue<Journey> journeysBetween = new PriorityQueue<>();

        for (Journey journey : schedule) {
            if (journey.from().equals(start) && journey.to().equals(destination)) {
                journeysBetween.add(journey);
            }
        }

        if (journeysBetween.isEmpty()) {
            throw new NoPathToDestinationException("There is no path satisfying the conditions!");
        }

        List<Journey> path = new LinkedList<>();
        path.add(journeysBetween.poll());

        return path;
    }

    private void validateJourneys(List<Journey> schedule) {
        if (schedule == null || schedule.isEmpty()) {
            throw new IllegalArgumentException("Schedule should contain at least one journey!");
        }

        for (Journey journey : schedule) {
            if (journey == null || journey.vehicleType() == null || journey.from() == null || journey.to() == null ||
                journey.price() == null) {
                throw new InvalidJourneyException("Journey arguments cannot be null!");
            }

            if (journey.price().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidJourneyException("Price cannot be negative or zero!");
            }

            if (journey.from().name() == null || journey.from().name().isEmpty() || journey.to().name() == null ||
                journey.to().name().isEmpty()) {
                throw new IllegalArgumentException("City name cannot be null or empty!");
            }

            if (journey.from().location() == null || journey.to().location() == null) {
                throw new IllegalArgumentException("City location cannot be null!");
            }

            if (journey.from().equals(journey.to())) {
                throw new InvalidJourneyException("Start and destination cannot be the same!");
            }
        }
    }

    public class CityComparator implements Comparator<City> {

        @Override
        public int compare(City first, City second) {
            BigDecimal firstCost = pathCosts.getOrDefault(first, new BigDecimal(Double.MAX_VALUE));
            BigDecimal secondCost = pathCosts.getOrDefault(second, new BigDecimal(Double.MAX_VALUE));

            return firstCost.compareTo(secondCost) != 0 ? firstCost.compareTo(secondCost) : first.compareTo(second);
        }
    }
}
