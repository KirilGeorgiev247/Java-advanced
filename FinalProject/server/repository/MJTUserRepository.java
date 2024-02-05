package server.repository;

import server.algorithm.HashCalculator;
import server.data.debt.Debt;
import server.data.group.Group;
import server.data.user.User;
import server.exception.AlreadyExistsException;
import server.exception.NotExistingGroupExeption;
import server.exception.NotExistingUserException;
import server.exception.NotExistingRelationshipException;
import server.response.Response;
import server.response.status.ServerStatusCode;
import server.storage.Storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MJTUserRepository implements UserRepository {

    private final Storage storage;

    private final HashCalculator hashCalculator;

    private static final String VALID_PASS_REGEX = "[a-zA-Z\\d]+";

    public MJTUserRepository(Storage storage, HashCalculator hashCalculator) {
        this.storage = storage;
        this.hashCalculator = hashCalculator;
    }

    @Override
    public Response addFriend(String clientUsername, String friendUsername) {
        if (clientUsername == null) {
            return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
        }

        try {
            User currUser = storage.getUser(clientUsername);

            if (!currUser.isLoggedIn()) {
                return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
            }

            User friendUser = storage.getUser(friendUsername);
            currUser.addFriendDebt(friendUser);
            friendUser.addFriendDebt(currUser);
        } catch (NotExistingUserException e) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, "User with such username does not exist!");
        } catch (AlreadyExistsException e) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, "This friend is already added");
        }

        return Response.ok(ServerStatusCode.OK, "Friend added successfully!");
    }

    @Override
    public Response announcePayOff(String clientUsername, BigDecimal amount, String friendUsername) {
        if (clientUsername == null) {
            return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
        }

        try {
            User currUser = storage.getUser(clientUsername);

            if (!currUser.isLoggedIn()) {
                return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
            }

            User friendUser = storage.getUser(friendUsername);

            friendUser.updateFriendDebt(currUser, amount, Optional.empty());
            currUser.updateFriendDebt(friendUser, amount.negate(), Optional.empty());
        } catch (NotExistingUserException | NotExistingRelationshipException e) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, e.getMessage());
        }

        return Response.ok(ServerStatusCode.OK, "Debts updated successfully!");
    }

    @Override
    public Response announceGroupPayOff(String clientUsername, BigDecimal amount, String groupName, String payer) {
        if (clientUsername == null) {
            return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
        }

        try {
            User currUser = storage.getUser(clientUsername);

            if (!currUser.isLoggedIn()) {
                return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
            }

            Group group = storage.getGroup(groupName);

            if (!group.groupCreator().equals(currUser.getUsername())) {
                return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not the owner of the group");
            }

            BigDecimal splitAmount =
                amount.divide(BigDecimal.valueOf(group.participants().size()), RoundingMode.CEILING);

            currUser.updateGroupDebt(group, splitAmount, Optional.empty());
        } catch (NotExistingUserException | NotExistingGroupExeption e) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, e.getMessage());
        }

        return Response.ok(ServerStatusCode.OK, "Debts updated successfully!");
    }

    @Override
    public Response getStatus(String username) {
        if (username == null) {
            return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
        }

        try {
            User currUser = storage.getUser(username);

            if (!currUser.isLoggedIn()) {
                return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
            }

            String info = getInfo(currUser);
            return Response.ok(ServerStatusCode.OK, info);

        } catch (NotExistingUserException e) {
            return Response.decline(ServerStatusCode.NOT_FOUND, "User with such username does not exist!");
        }
    }

    @Override
    public Response split(String clientUsername, String friendUsername, BigDecimal amount, Optional<String> reason) {
        if (clientUsername == null) {
            return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
        }

        try {
            User currUser = storage.getUser(clientUsername);

            if (!currUser.isLoggedIn()) {
                return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
            }

            User friendUser = storage.getUser(friendUsername);

            friendUser.updateFriendDebt(currUser, amount.negate(), reason);
            currUser.updateFriendDebt(friendUser, amount, reason);
        } catch (NotExistingUserException | NotExistingRelationshipException e) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, e.getMessage());
        }

        return Response.ok(ServerStatusCode.OK, "Debts updated successfully!");
    }

    @Override
    public Response splitGroup(String clientUsername, String groupName, BigDecimal amount, Optional<String> reason) {
        if (clientUsername == null) {
            return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
        }

        try {
            User currUser = storage.getUser(clientUsername);

            if (!currUser.isLoggedIn()) {
                return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
            }

            Group group = storage.getGroup(groupName);

            if (!group.groupCreator().equals(currUser.getUsername())) {
                return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not the group creator!");
            }

            currUser.updateGroupDebt(group, amount, reason);
        } catch (NotExistingUserException | NotExistingGroupExeption e) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, e.getMessage());
        }

        return Response.ok(ServerStatusCode.OK, "Debts updated successfully!");
    }

    @Override
    public Response createGroup(String clientUsername, String groupName, List<String> usernames) {
        if (clientUsername == null) {
            return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
        }

        try {
            User currUser = storage.getUser(clientUsername);

            if (!currUser.isLoggedIn()) {
                return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
            }

            List<User> participants = new ArrayList<>();

            for(String username : usernames) {
                participants.add(storage.getUser(username));
            }

            Group group = new Group(groupName, clientUsername, participants);
            storage.addGroup(group);

            return Response.ok(ServerStatusCode.OK, "Group " + groupName + " created!");
        } catch (NotExistingUserException | AlreadyExistsException e) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public Response createUser(String clientUsername, String username, String password, String rePassword) {
        if (clientUsername != null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, "You are currently logged!");
        }

        try {
            validatePassword(password, rePassword);

            String hashedPass;

            try(InputStream inputStream = new ByteArrayInputStream(password.getBytes())) {
                hashedPass = hashCalculator.calculate(inputStream);
            }

            User user = new User(username, hashedPass);

            storage.addUser(user);

            return Response.ok(ServerStatusCode.OK, "Successful registration!");
        } catch (IllegalArgumentException | AlreadyExistsException e) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, e.getMessage());
        } catch (NoSuchAlgorithmException | IOException e) {
            return Response.decline(ServerStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Response loginUser(String clientUsername, String username, String password) {
        if (clientUsername == null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, "You are currently logged!");
        }

        try {
            String hashedPass;

            try(InputStream inputStream = new ByteArrayInputStream(password.getBytes())) {
                hashedPass = hashCalculator.calculate(inputStream);
            }

            User currUser = storage.getUser(username);

            if (!hashedPass.equals(currUser.getHashedPass())) {
                throw new IllegalArgumentException("Invalid password!");
            }

            currUser.setLogIn();

            return Response.ok(ServerStatusCode.OK, "Successful login!");
        } catch (IllegalArgumentException | NotExistingUserException e) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, e.getMessage());
        } catch (NoSuchAlgorithmException | IOException e) {
            return Response.decline(ServerStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Response logOutUser(String username) {
        if (username == null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, "You are not logged!");
        }

        try {
            User currUser = storage.getUser(username);

            currUser.setLogIn();

            return Response.ok(ServerStatusCode.OK, "Successful logout!");
        } catch (NotExistingUserException e) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, e.getMessage());
        }
    }

    private String getInfo(User currUser) {
        StringBuilder sb = new StringBuilder();

        getFriendDebtsInfo(currUser, sb);
        getGroupDebtsInfo(currUser, sb);

        if (sb.isEmpty()) {
            return "You have no debts!";
        }

        return sb.toString();
    }

    private void getFriendDebtsInfo(User currUser, StringBuilder sb) {
        if (!currUser.getFriendDebts().isEmpty()) {
            sb.append("Friends: ").append(System.lineSeparator());

            currUser.getFriendDebts().entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().getAmount()))
                .filter(e -> e.getValue().getAmount().compareTo(BigDecimal.ZERO) > 0)
                .forEach(e -> sb.append("* You owe ").append(e.getKey()).append(" ").append(e.getValue().getAmount())
                    .append(" [ ").append(e.getValue().getReason()).append(" ]").append(System.lineSeparator()));

            currUser.getFriendDebts().entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().getAmount()))
                .filter(e -> e.getValue().getAmount().compareTo(BigDecimal.ZERO) < 0)
                .forEach(e -> sb.append("* ").append(e.getKey()).append(" owes you ").append(e.getValue().getAmount())
                    .append(" [ ").append(e.getValue().getReason()).append(" ]").append(System.lineSeparator()));

            sb.append(System.lineSeparator());
        }
    }

    private void getGroupDebtsInfo(User currUser, StringBuilder sb) {
        if (!currUser.getGroupDebts().isEmpty()) {
            sb.append("Groups: ").append(System.lineSeparator());

            for (Map.Entry<String, List<Debt>> entry : currUser.getGroupDebts().entrySet()) {
                sb.append("* ").append(entry.getKey()).append(System.lineSeparator());

                entry.getValue().stream().sorted(Comparator.comparing(Debt::getAmount))
                    .filter(e -> e.getAmount().compareTo(BigDecimal.ZERO) > 0)
                    .forEach(e -> sb.append("You owe ").append(e.getOwning()).append(" ").append(e.getAmount())
                        .append(" [ ").append(e.getReason()).append(" ]").append(System.lineSeparator()));

                entry.getValue().stream().sorted(Comparator.comparing(Debt::getAmount))
                    .filter(e -> e.getAmount().compareTo(BigDecimal.ZERO) < 0)
                    .forEach(e -> sb.append(e.getOwning()).append(" owes you").append(e.getAmount())
                        .append(" [ ").append(e.getReason()).append(" ]").append(System.lineSeparator()));

                sb.append(System.lineSeparator());
            }
        }
    }

    private void validatePassword(String password, String rePassword) {
        if(!password.equals(rePassword)) {
            throw new IllegalArgumentException("Password and rePassword must match!");
        }

        if (password.length() < 6 || password.length() > 20) {
            throw new IllegalArgumentException("Password should be between 6 and 20 characters!");
        }

        if (!password.matches(VALID_PASS_REGEX)) {
            throw new IllegalArgumentException("Password must contain only letters and digits!");
        }
    }
}
