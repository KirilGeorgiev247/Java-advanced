package server.command;

import java.util.Arrays;
import java.util.List;

public class CommandCreator {
    private static final String WHITESPACE_PATTERN = "\\s+";
    private static List<String> getCommandArguments(String input) {
        return Arrays.stream(input.split(WHITESPACE_PATTERN)).toList();
    }

    public static Command newCommand(String clientInput) {
        List<String> tokens = CommandCreator.getCommandArguments(clientInput);
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

        return new Command(tokens.get(0), args);
    }
}
