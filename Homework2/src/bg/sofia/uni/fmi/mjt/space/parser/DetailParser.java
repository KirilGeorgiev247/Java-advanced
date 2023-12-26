package bg.sofia.uni.fmi.mjt.space.parser;

import bg.sofia.uni.fmi.mjt.space.mission.Detail;

public class DetailParser {
    private static final int ROCKET_NAME_INDEX = 0;
    private static final int PAYLOAD_INDEX = 1;
    private static final String propsRegexPattern = " [|] ";

    public static Detail extractDetail(String info) {
        String[] props = info.split(propsRegexPattern);
        String rocketName = props[ROCKET_NAME_INDEX];
        String payload = props[PAYLOAD_INDEX];
        return new Detail(rocketName, payload);
    }
}
