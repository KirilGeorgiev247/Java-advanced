package bg.sofia.uni.fmi.mjt.space.rocket;

import bg.sofia.uni.fmi.mjt.space.parser.RocketParser;

import java.util.Optional;

// TODO: what is публичен каноничен конструктор
public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {

    public static Rocket of(String line) {
        return RocketParser.extractRocket(line);
    }
}
