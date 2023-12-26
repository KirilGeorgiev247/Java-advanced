package bg.sofia.uni.fmi.mjt.space.parser;

import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;

import java.util.Optional;

public class RocketParser {

    private static final int CORRECT_PROPS_COUNT = 4;
    private static final int ID_INDEX = 0;
    private static final int NAME_INDEX = 1;
    private static final int WIKI_INDEX = 2;
    private static final int HEIGHT_INDEX = 3;

    private static final String propsRegexPattern = ",";

    public static Rocket extractRocket(String info) {
        String[] props = info.split(propsRegexPattern);

        String id = props[ID_INDEX];
        String name = props[NAME_INDEX];
        Optional<String> wiki = Optional.empty();
        if (props[WIKI_INDEX] != null && !props[WIKI_INDEX].isBlank() && !props[WIKI_INDEX].isEmpty()) {
            wiki = Optional.of(props[WIKI_INDEX]);
        }
        Optional<Double> height = Optional.empty();

        if (props.length == CORRECT_PROPS_COUNT) {
            try {
                height = Optional.of(Double.parseDouble(props[HEIGHT_INDEX].replace("m", "")));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Height format is invalid!", e);
            }
        }

        return new Rocket(id, name, wiki, height);
    }
}
