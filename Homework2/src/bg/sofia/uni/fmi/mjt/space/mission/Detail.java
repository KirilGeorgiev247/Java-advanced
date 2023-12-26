package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.parser.DetailParser;

public record Detail(String rocketName, String payload) {
    public static Detail of(String info) {
        return DetailParser.extractDetail(info);
    }
}
