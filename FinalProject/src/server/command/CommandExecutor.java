package server.command;

import server.repository.UserRepository;
import server.response.Response;
import server.response.status.ServerStatusCode;
import server.session.Session;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandExecutor {
    private static final int THREE_COMMAND_ARGUMENTS = 3;
    private static final int MIN_GROUP_COUNT = 3;
    private static final int TWO_COMMAND_ARGUMENTS = 2;
    private static final String VALID_NUMBER_REGEX = "-?\\d+(\\.\\d+)?";
    private static final String REGISTER = "register";
    private static final String LOGIN = "login";
    private static final String LOG_OUT = "logout";
    private static final String ADD_FRIEND = "add-friend";
    private static final String CREATE_GROUP = "create-group";
    private static final String SPLIT = "split";
    private static final String SPLIT_GROUP = "split-group";
    private static final String GET_STATUS = "get-status";
    private static final String PAYED = "payed";
    private static final String PAYMENT_HISTORY = "payment-history";
    private static final String DISCONNECT = "disconnect";
    private static final String HELP = "help";

    private final UserRepository userRepository;

    public CommandExecutor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Response execute(Command cmd, Session session) {
        return switch (cmd.command()) {
            case REGISTER -> register(cmd.arguments(), session);
            case LOGIN -> login(cmd.arguments(), session);
            case LOG_OUT -> logOut(cmd.arguments(), session);
            case ADD_FRIEND -> addFriend(cmd.arguments(), session);
            case CREATE_GROUP -> createGroup(cmd.arguments(), session);
            case SPLIT -> split(cmd.arguments(), session);
            case SPLIT_GROUP -> splitGroup(cmd.arguments(), session);
            case GET_STATUS -> getStatus(cmd.arguments(), session);
            case PAYED -> payed(cmd.arguments(), session);
            case PAYMENT_HISTORY -> paymentHistory(cmd.arguments(), session);
            case DISCONNECT -> disconnect(cmd.arguments());
            case HELP -> getCommands(cmd.arguments());
            default -> Response.decline(ServerStatusCode.BAD_REQUEST, "Unknown command!");
        };
    }

    // register <username> <password> <rePassword>
    private Response register(String[] args, Session session) {
        if (args.length != THREE_COMMAND_ARGUMENTS) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Register arguments are less or more! Example: register <username> <password> <rePassword>");
        }

        if (args[0] == null || args[1] == null || args[2] == null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Payed group arguments are invalid! Example: register <username> <password> <rePassword>");
        }

        Response response = userRepository.createUser(session.getUsername(), args[0], args[1], args[2]);

        if (response.statusCode() == ServerStatusCode.OK.getCode()) {
            session.setUsername(args[0]);
        }

        return response;
    }

    // login <username> <password>
    private Response login(String[] args, Session session) {
        if (args.length != TWO_COMMAND_ARGUMENTS) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Login arguments are less or more! Example: login <username> <password>");
        }

        if (args[0] == null || args[1] == null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Login arguments are invalid! Example: login <username> <password>");
        }

        Response response = userRepository.loginUser(session.getUsername(), args[0], args[1]);

        if (response.statusCode() == ServerStatusCode.OK.getCode()) {
            session.setUsername(args[0]);
        }

        return response;
    }

    // logout
    private Response logOut(String[] args, Session session) {
        if (args.length != 0) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Logout command does not take any arguments! Example: logout");
        }

        Response response = userRepository.logOutUser(session.getUsername());

        if (response.statusCode() == ServerStatusCode.OK.getCode()) {
            session.setUsername(null);
        }

        return response;
    }

    // add-friend <username>
    private Response addFriend(String[] args, Session session) {
        if (args.length != 1) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Add friend arguments are less or more! Example: add-friend <username>");
        }

        if (args[0] == null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Add friend argument is invalid! Example: add-friend <username>");
        }

        return userRepository.addFriend(session.getUsername(), args[0]);
    }

    // create-group <group_name> <username> <username> ... <username>
    private Response createGroup(String[] args, Session session) {
        if (args.length < TWO_COMMAND_ARGUMENTS) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Create group arguments are less! Example: create-group <group_name> <username> [<username> ...]");
        }

        List<String> participantUsernames = Arrays.stream(args).toList().subList(1, args.length);

        if (participantUsernames.isEmpty() || participantUsernames.size() < 2 ||
            participantUsernames.size() == 2 && session.getUsername() == null ||
            participantUsernames.size() == 2 && session.getUsername() != null &&
                participantUsernames.contains(session.getUsername()) ||
            participantUsernames.size() < MIN_GROUP_COUNT && session.getUsername() == null &&
                !participantUsernames.contains(session.getUsername())) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Group should be created with at least 3 people including yourself!");
        }

        return userRepository.createGroup(session.getUsername(), args[0],
            participantUsernames);
    }

    // split <amount> <username> <reason_for_payment>
    private Response split(String[] args, Session session) {
        if (args.length < THREE_COMMAND_ARGUMENTS) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Split arguments are less! Example: split <amount> <username> <reason_for_payment>");
        }

        if (!args[0].matches(VALID_NUMBER_REGEX)) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Split amount is invalid! Example: split <amount> <username> <reason_for_payment>");
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        return userRepository.split(session.getUsername(), args[1], new BigDecimal(args[0]), Optional.of(reason));
    }

    // split-group <amount> <group_name> <reason_for_payment>
    private Response splitGroup(String[] args, Session session) {
        if (args.length < THREE_COMMAND_ARGUMENTS) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Split group arguments are less! Example: split-group <amount> <group_name> <reason_for_payment>");
        }

        if (!args[0].matches(VALID_NUMBER_REGEX)) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Split group amount is invalid! Example: split-group <amount> <group_name> <reason_for_payment>");
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        return userRepository.splitGroup(session.getUsername(), args[1], new BigDecimal(args[0]), Optional.of(reason));
    }

    // get-status
    private Response getStatus(String[] args, Session session) {
        if (args.length != 0) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Get status command does not take any arguments! Example: get-status");
        }

        return userRepository.getStatus(session.getUsername());
    }

    // payed <amount> <username>
    private Response payed(String[] args, Session session) {
        if (args.length != TWO_COMMAND_ARGUMENTS) {
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

        return userRepository.announcePayOff(session.getUsername(), new BigDecimal(args[0]), args[1]);
    }

    // payment-history
    private Response paymentHistory(String[] args, Session session) {
        if (args.length != 0) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Payment history command does not take any arguments! Example: payment-history");
        }

        return userRepository.paymentHistory(session.getUsername());
    }

    // help
    private Response getCommands(String[] args) {
        if (args.length != 0) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Help command does not take any arguments! Example: help");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Available commands:").append(System.lineSeparator());
        sb.append("register <username> <password> <rePassword> - register a new user.").append(System.lineSeparator());
        sb.append("login <username> <password> - log in as an existing user.").append(System.lineSeparator());
        sb.append("logout - log out of the current session.").append(System.lineSeparator());
        sb.append("add-friend <username> - add a user as a friend.").append(System.lineSeparator());
        sb.append("create-group <group_name> <username> <username> ... - create a new group with specified members.")
            .append(System.lineSeparator());
        sb.append("split <amount> <username> <reason_for_payment> - split a bill with a friend.")
            .append(System.lineSeparator());
        sb.append("split-group <amount> <group_name> <reason_for_payment> - split a bill with a group.")
            .append(System.lineSeparator());
        sb.append("get-status - get your status.").append(System.lineSeparator());
        sb.append("payed <amount> <username> - announce a payment made by a friend.").append(System.lineSeparator());
        sb.append("payment-history - show your payment actions history.").append(System.lineSeparator());
        sb.append("disconnect - disconnect from the server.").append(System.lineSeparator());
        sb.append("help - show this help message.").append(System.lineSeparator());

        return Response.ok(ServerStatusCode.OK, sb.toString());
    }

    // disconnect
    private Response disconnect(String[] args) {
        if (args.length != 0) {
            return Response.decline(ServerStatusCode.BAD_REQUEST,
                "Disconnect command does not take any arguments! Example: disconnect");
        }

        return Response.ok(ServerStatusCode.OK, "Disconnected");
    }
}
