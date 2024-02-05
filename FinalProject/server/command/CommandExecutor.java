package server.command;

import server.repository.UserRepository;
import server.response.Response;
import server.response.status.ServerStatusCode;
import server.session.Session;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

public class CommandExecutor {
//    private static final String INVALID_ARGS_COUNT_MESSAGE_FORMAT =
//        "Invalid command syntax: \"%s\" expects: \"%s\"";

    private static final String VALID_NUMBER_REGEX = "-?\\d+(\\.\\d+)?";

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

    // register <username> <password> <rePassword>
    private Response register(String[] args, String clientUsername) {
        if (args.length != 3) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Register arguments are less or more! Example: register <username> <password> <rePassword>");
        }

        if (args[0] == null || args[1] == null || args[2] == null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, "Payed group arguments are invalid! Example: register <username> <password> <rePassword>");
        }

        return userRepository.createUser(clientUsername, args[0], args[1], args[2]);
    }

    // login <username> <password>
    private Response login(String[] args, String clientUsername) {
        if (args.length != 2) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Login arguments are less or more! Example: login <username> <password>");
        }

        if (args[0] == null || args[1] == null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Login arguments are invalid! Example: login <username> <password>");
        }

        return userRepository.loginUser(clientUsername, args[0], args[1]);
    }

    // logout
    private Response logOut(String[] args, String clientUsername) {
        if (args.length != 0) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Logout command does not take any arguments! Example: logout");
        }

        return userRepository.logOutUser(clientUsername);
    }

    // add-friend <username>
    private Response addFriend(String[] args, String clientUsername) {
        if (args.length != 1) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Add friend arguments are less or more! Example: add-friend <username>");
        }

        if (args[0] == null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Add friend argument is invalid! Example: add-friend <username>");
        }

        return userRepository.addFriend(clientUsername, args[0]);
    }

    // create-group <group_name> <username> <username> ... <username>
    private Response createGroup(String[] args, String clientUsername) {
        if (args.length < 2) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Create group arguments are less! Example: create-group <group_name> <username> [<username> ...]");
        }

        return userRepository.createGroup(clientUsername, args[0], Arrays.stream(args).toList().subList(1, args.length));
    }

    // split <amount> <username> <reason_for_payment>
    private Response split(String[] args, String clientUsername) {
        if (args.length < 3) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Split arguments are less! Example: split <amount> <username> <reason_for_payment>");
        }

        if (!args[0].matches(VALID_NUMBER_REGEX)) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Split amount is invalid! Example: split <amount> <username> <reason_for_payment>");
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        return userRepository.split(clientUsername, args[0], new BigDecimal(args[1]), Optional.of(reason));
    }

    // split-group <amount> <group_name> <reason_for_payment>
    private Response splitGroup(String[] args, String clientUsername) {
        if (args.length < 3) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Split group arguments are less! Example: split-group <amount> <group_name> <reason_for_payment>");
        }

        if (!args[0].matches(VALID_NUMBER_REGEX)) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Split group amount is invalid! Example: split-group <amount> <group_name> <reason_for_payment>");
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        return userRepository.splitGroup(clientUsername, args[0], new BigDecimal(args[1]), Optional.of(reason));
    }

    // get-status
    private Response getStatus(String[] args, String clientUsername) {
        if (args.length != 0) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Get status command does not take any arguments! Example: get-status");
        }

        return userRepository.getStatus(clientUsername);
    }

    // payed <amount> <username>
    private Response payed(String[] args, String clientUsername) {
        if (args.length != 2) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Pay arguments are less or more! Example: payed <amount> <username>");
        }

        if (args[0] == null || args[1] == null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Pay arguments are invalid! Example: payed <amount> <username>");
        }

        if (!args[0].matches(VALID_NUMBER_REGEX)) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Pay amount is invalid! Example: payed <amount> <username>");
        }

        return userRepository.announcePayOff(clientUsername, new BigDecimal(args[0]), args[1]);
    }

    // payed-group <amount> <groupName> <username>
    private Response payedGroup(String[] args, String clientUsername) {
        if (args.length != 3) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Payed group arguments are less or more! Example: payed-group <amount> <groupName> <username>");
        }

        if (args[0] == null || args[1] == null || args[2] == null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Payed group arguments are invalid! Example: payed-group <amount> <groupName> <username>");
        }

        if (!args[0].matches(VALID_NUMBER_REGEX)) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Payed group amount is invalid! Example: payed-group <amount> <groupName> <username>");
        }

        return userRepository.announceGroupPayOff(clientUsername, new BigDecimal(args[0]), args[1], args[2]);
    }
}
