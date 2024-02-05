package server.command;

import server.repository.UserRepository;
import server.response.Response;
import server.response.status.ServerStatusCode;
import server.session.Session;

import java.math.BigDecimal;
import java.util.Arrays;

public class CommandExecutor {
    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
        "Invalid command syntax: \"%s\" expects: \"%s\"";

    // TODO: command names -->
    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String LOG_OUT = "logout";
    private static final String ADD_FRIEND = "add-friend";
    private static final String CREATE_GROUP = "create-group";
    private static final String SPLIT = "split";
    private static final String SPLIT_GROUP = "split-group";
    private static final String GET_STATUS = "get-status";
    private static final String PAYED = "payed";
    private static final String PAYED_GROUP = "payed-group";

    // TODO: command names <--

    private UserRepository userRepository;

    public CommandExecutor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response execute(Command cmd, Session session) {
        return switch (cmd.command()) {
            case REGISTER -> register(cmd.arguments(), session.getUsername());
            case LOGIN -> login(cmd.arguments(), session.getUsername());
            case LOG_OUT -> logOut(cmd.arguments(), session.getUsername());
            case ADD_FRIEND -> addFriend(cmd.arguments(), session.getUsername());
            case CREATE_GROUP -> createGroup(cmd.arguments(), session.getUsername());
            case SPLIT -> split(cmd.arguments(), session.getUsername());
            case SPLIT_GROUP -> splitGroup(cmd.arguments(), session.getUsername());
            case GET_STATUS -> getStatus(cmd.arguments(), session.getUsername());
            case PAYED -> payed(cmd.arguments(), session.getUsername());
            case PAYED_GROUP -> payedGroup(cmd.arguments(), session.getUsername());
            default -> Response.decline(ServerStatusCode.BAD_REQUEST, "Unknown command!");
        };
    }

    private Response register(String[] args, String clientUsername) {
        // validate

        return userRepository.createUser(clientUsername, args[0], args[1], args[2]);
    }

    private Response login(String[] args, String clientUsername) {
        // validate


        return userRepository.loginUser(clientUsername, args[0], args[1]);
    }

    private Response logOut(String[] args, String clientUsername) {
        // validate

        return userRepository.logOutUser(clientUsername);
    }

    private Response addFriend(String[] args, String clientUsername) {
        // validate

        return userRepository.addFriend(clientUsername, args[0]);
    }

    private Response createGroup(String[] args, String clientUsername) {
        // validate

        return userRepository.createGroup(clientUsername, args[0], Arrays.stream(args).toList().subList(1, args.length));
    }

    private Response split(String[] args, String clientUsername) {
        // validate

        return userRepository.split(clientUsername, args[0], new BigDecimal(args[1]), );
    }

    private Response splitGroup(String[] args, String clientUsername) {
        // validate

        return userRepository.splitGroup(clientUsername, args[0], new BigDecimal(args[1]), );
    }

    private Response getStatus(String[] args, String clientUsername) {
        if (args.length != 0) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Get status command does not take any arguments! Example: get-status");
        }

        return userRepository.getStatus(clientUsername);
    }

    private Response payed(String[] args, String clientUsername) {
        // validate

        return userRepository.announcePayOff(clientUsername, new BigDecimal(args[0]), args[1]);
    }

    private Response payedGroup(String[] args, String clientUsername) {
        

        return userRepository.announceGroupPayOff(clientUsername, new BigDecimal(args[0]), args[1], args[2]);
    }
}
