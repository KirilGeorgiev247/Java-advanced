package server.repository;

import logger.Logger;
import server.algorithm.HashCalculator;
import server.data.debt.Debt;
import server.data.debt.DebtType;
import server.data.group.Group;
import server.data.notification.Notification;
import server.data.notification.NotificationType;
import server.data.user.User;
import server.exception.AlreadyExistsException;
import server.exception.NotExistingGroupExeption;
import server.exception.NotExistingRelationshipException;
import server.exception.NotExistingUserException;
import server.response.Response;
import server.response.status.ServerStatusCode;
import server.services.NotificationService;
import server.storage.Storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class MJTUserRepository implements UserRepository {

    private static final int MIN_PASS_LEN = 6;
    private static final int MAX_PASS_LEN = 20;
    private static final String VALID_PASS_REGEX = "[a-zA-Z\\d]+";
    private final Storage storage;
    private final HashCalculator hashCalculator;
    private final NotificationService notificationService;

    public MJTUserRepository(Storage storage, HashCalculator hashCalculator, NotificationService notificationService) {
        this.storage = storage;
        this.hashCalculator = hashCalculator;
        this.notificationService = notificationService;
    }

    @Override
    public Response addFriend(String clientUsername, String friendUsername) {
        if (clientUsername == null) {
            return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
        }

        try {
            User currUser = storage.getUser(clientUsername);

            User friendUser = storage.getUser(friendUsername);

            currUser.addDebt(friendUser, DebtType.FRIEND, Optional.empty());
            friendUser.addDebt(currUser, DebtType.FRIEND, Optional.empty());

            notificationService.notifyAddFriend(currUser, friendUser);
            storage.save();
        } catch (NotExistingUserException | AlreadyExistsException e) {
            Logger.logError(e.getMessage(), e);
            return Response.decline(ServerStatusCode.BAD_REQUEST, e.getMessage());
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

            if (!currUser.getDebts().containsKey(friendUsername)) {
                return Response.decline(ServerStatusCode.BAD_REQUEST, "You are not in a relationship with this user!");
            }

            User friendUser = storage.getUser(friendUsername);

            friendUser.updateDebt(currUser, amount.negate(), Optional.empty());
            currUser.updateDebt(friendUser, amount, Optional.empty());

            storage.addPaymentActionToHistory(friendUser,
                new Notification(NotificationType.FRIEND, "You paid " + amount + " to " + clientUsername));
            notificationService.notifyPayOff(clientUsername, friendUser, amount);
            storage.save();
        } catch (NotExistingUserException | NotExistingRelationshipException e) {
            Logger.logError(e.getMessage(), e);
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

            String info = getInfo(currUser);
            return Response.ok(ServerStatusCode.OK, info);
        } catch (NotExistingUserException e) {
            Logger.logError(e.getMessage(), e);
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

            User friendUser = storage.getUser(friendUsername);

            if (!currUser.getDebts().containsKey(friendUser.getUsername())) {
                return Response.decline(ServerStatusCode.BAD_REQUEST, "You have no relationship with this user!");
            }

            BigDecimal splitAmount = amount.divide(BigDecimal.TWO, 2, RoundingMode.HALF_UP);

            friendUser.updateDebt(currUser, splitAmount, reason);
            currUser.updateDebt(friendUser, splitAmount.negate(), reason);

            notificationService.notifySplit(currUser, friendUser, splitAmount);
            storage.save();
        } catch (NotExistingUserException | NotExistingRelationshipException e) {
            Logger.logError(e.getMessage(), e);
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
            Group group = storage.getGroup(groupName);

            if (!group.groupCreator().equals(currUser.getUsername())) {
                return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not the group creator!");
            }

            BigDecimal splitAmount =
                amount.divide(BigDecimal.valueOf(group.participants().size()), 2, RoundingMode.HALF_UP);

            updateDebts(group, currUser, splitAmount, reason);

            notificationService.notifyGroupSplit(currUser, group, splitAmount);
            storage.save();
        } catch (NotExistingUserException | NotExistingGroupExeption | NotExistingRelationshipException e) {
            Logger.logError(e.getMessage(), e);
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

            List<User> participants = new ArrayList<>();

            for (String username : usernames) {
                participants.add(storage.getUser(username));
            }

            if (!participants.contains(currUser)) {
                participants.add(currUser);
            }
            Group group = new Group(groupName, clientUsername, participants);
            addDebts(group, currUser);
            notificationService.notifyGroupCreation(currUser, group);
            storage.addGroup(group);
            storage.save();
            return Response.ok(ServerStatusCode.OK, "Group " + groupName + " created!");
        } catch (NotExistingUserException | AlreadyExistsException e) {
            Logger.logError(e.getMessage(), e);
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

            try (InputStream inputStream = new ByteArrayInputStream(password.getBytes())) {
                hashedPass = hashCalculator.calculate(inputStream);
            }

            User user = new User(username, hashedPass);

            storage.addUser(user);

            return Response.ok(ServerStatusCode.OK, "Successful registration!");
        } catch (IllegalArgumentException | AlreadyExistsException e) {
            Logger.logError(e.getMessage(), e);
            return Response.decline(ServerStatusCode.BAD_REQUEST, e.getMessage());
        } catch (NoSuchAlgorithmException | IOException e) {
            Logger.logError(e.getMessage(), e);
            return Response.decline(ServerStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Response loginUser(String clientUsername, String username, String password) {
        if (clientUsername != null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, "You are currently logged!");
        }

        try {
            String hashedPass;
            try (InputStream inputStream = new ByteArrayInputStream(password.getBytes())) {
                hashedPass = hashCalculator.calculate(inputStream);
            }

            User currUser = storage.getUser(username);
            if (!hashedPass.equals(currUser.getHashedPass())) {
                throw new IllegalArgumentException("Invalid password!");
            }

            String loginInfo = getLoginInfo(currUser);
            currUser.clearNotifications();
            storage.save();

            return Response.ok(ServerStatusCode.OK, loginInfo);
        } catch (IllegalArgumentException | NotExistingUserException e) {
            Logger.logError(e.getMessage(), e);
            return Response.decline(ServerStatusCode.BAD_REQUEST, e.getMessage());
        } catch (NoSuchAlgorithmException | IOException e) {
            Logger.logError(e.getMessage(), e);
            return Response.decline(ServerStatusCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public Response logOutUser(String username) {
        if (username == null) {
            return Response.decline(ServerStatusCode.BAD_REQUEST, "You are not logged!");
        }

        return Response.ok(ServerStatusCode.OK, "Successful logout!");
    }

    @Override
    public Response paymentHistory(String username) {
        if (username == null) {
            return Response.decline(ServerStatusCode.UNAUTHORIZED, "You are not logged in!");
        }

        try {
            User currUser = storage.getUser(username);

            String paymentHistoryInfo = getHistoryInfo(currUser);
            return Response.ok(ServerStatusCode.OK, paymentHistoryInfo);
        } catch (NotExistingUserException e) {
            Logger.logError(e.getMessage(), e);
            return Response.decline(ServerStatusCode.NOT_FOUND, "User with such username does not exist!");
        }
    }

    private void addDebts(Group group, User currUser)
        throws AlreadyExistsException {
        for (User participant : group.participants()) {
            if (!participant.getUsername().equals(currUser.getUsername())) {
                participant.addDebt(currUser, DebtType.GROUP, Optional.of(group.name()));
            }
        }

        for (User participant : group.participants()) {
            if (!participant.getUsername().equals(currUser.getUsername())) {
                currUser.addDebt(participant, DebtType.GROUP, Optional.of(group.name()));
            }
        }
    }

    private void updateDebts(Group group, User currUser, BigDecimal splitAmount, Optional<String> reason)
        throws NotExistingRelationshipException {
        for (User participant : group.participants()) {
            if (!participant.getUsername().equals(currUser.getUsername())) {
                participant.updateDebt(currUser, splitAmount, reason);
            }
        }

        for (User participant : group.participants()) {
            if (!participant.getUsername().equals(currUser.getUsername())) {
                currUser.updateDebt(participant, splitAmount.negate(), reason);
            }
        }
    }

    private String getHistoryInfo(User currUser) {
        StringBuilder sb = new StringBuilder();

        sb.append("Payment actions history:").append(System.lineSeparator());

        Queue<Notification> history = storage.getUserPaymentHistory(currUser);

        if (history == null || history.isEmpty()) {
            sb.append("No payment actions.").append(System.lineSeparator());
        } else {
            for (Notification notification : history) {
                sb.append(notification.message()).append(System.lineSeparator());
            }
        }

        return sb.toString().trim();
    }

    private String getLoginInfo(User currUser) {
        StringBuilder sb = new StringBuilder();
        sb.append("Successful login!").append(System.lineSeparator());
        sb.append("**Notifications**").append(System.lineSeparator());
        if (currUser.getNotifications().isEmpty()) {
            sb.append("No notifications to show.");
        } else {
            boolean hasFriendNotifications = currUser.getNotifications().stream()
                .anyMatch(n -> n.type().equals(NotificationType.FRIEND));
            if (hasFriendNotifications) {
                sb.append("Friends:").append(System.lineSeparator());
                currUser.getNotifications().stream().filter(
                        n -> n.type().equals(NotificationType.FRIEND) &&
                            !n.message().equals(System.lineSeparator()))
                    .forEach(n -> sb.append(n.message()).append(System.lineSeparator()));
            }

            boolean hasGroupNotifications = currUser.getNotifications().stream()
                .anyMatch(n -> n.type().equals(NotificationType.GROUP));
            if (hasGroupNotifications) {
                sb.append("Groups:").append(System.lineSeparator());
                currUser.getNotifications().stream().filter(n -> n.type().equals(NotificationType.GROUP) &&
                        !n.message().equals(System.lineSeparator()))
                    .forEach(n -> sb.append(n.message()).append(System.lineSeparator()));
            }
        }

        return sb.toString().trim();
    }

    private String getInfo(User currUser) {
        StringBuilder sb = new StringBuilder();

        getFriendDebtsInfo(currUser, sb);
        getGroupDebtsInfo(currUser, sb);

        if (sb.isEmpty()) {
            return "You have no debts!";
        }

        return sb.toString().trim();
    }

    private void getFriendDebtsInfo(User currUser, StringBuilder sb) {
        if (!currUser.getDebts().isEmpty() && currUser.getDebts().values().stream()
            .anyMatch(
                debt -> debt.getAmount().compareTo(BigDecimal.ZERO) != 0 && debt.getType().equals(DebtType.FRIEND))) {
            sb.append("Friends: ").append(System.lineSeparator());

            currUser.getDebts().entrySet().stream()
                .filter(e -> e.getValue().getType().equals(DebtType.FRIEND))
                .sorted(Comparator.comparing(e -> e.getValue().getAmount()))
                .forEach(e -> {
                    if (e.getValue().getAmount().compareTo(BigDecimal.ZERO) > 0) {
                        String line = String.format("* %s owes you %s [%s]", e.getKey(), e.getValue().getAmount(),
                            String.join(", ", e.getValue().getReasons()));
                        sb.append(line).append(System.lineSeparator());
                    } else if (e.getValue().getAmount().compareTo(BigDecimal.ZERO) < 0) {
                        String line =
                            String.format("* You owe %s %s [%s]", e.getKey(), e.getValue().getAmount().negate(),
                                String.join(", ", e.getValue().getReasons()));
                        sb.append(line).append(System.lineSeparator());
                    }
                });
        }
    }

    private void getGroupDebtsInfo(User currUser, StringBuilder sb) {
        Map<String, List<Debt>> groupDebtsByGroupName = new HashMap<>();

        currUser.getDebts().values().stream()
            .filter(debt -> debt.getType().equals(DebtType.GROUP) && debt.getGroupName() != null &&
                !debt.getGroupName().isBlank() && debt.getAmount().compareTo(BigDecimal.ZERO) != 0)
            .forEach(debt -> groupDebtsByGroupName.computeIfAbsent(debt.getGroupName(), k -> new ArrayList<>())
                .add(debt));

        if (!groupDebtsByGroupName.isEmpty()) {
            sb.append("Groups: ").append(System.lineSeparator());
            groupDebtsByGroupName.forEach((groupName, debts) -> {
                sb.append("* ").append(groupName).append(":").append(System.lineSeparator());
                debts.forEach(debt -> {
                    String debtLine = debt.getAmount().compareTo(BigDecimal.ZERO) < 0 ?
                        String.format("  You owe %s %s LV [%s].", debt.getOwning(), debt.getAmount().negate(),
                            String.join(", ", debt.getReasons())) :
                        String.format("  %s owes you %s LV [%s].", debt.getOwning(), debt.getAmount(),
                            String.join(", ", debt.getReasons()));
                    sb.append(debtLine).append(System.lineSeparator());
                });
            });
        }
    }

    private void validatePassword(String password, String rePassword) {
        if (!password.equals(rePassword)) {
            throw new IllegalArgumentException("Password and rePassword must match!");
        }

        if (password.length() < MIN_PASS_LEN || password.length() > MAX_PASS_LEN) {
            throw new IllegalArgumentException("Password should be between 6 and 20 characters!");
        }

        if (!password.matches(VALID_PASS_REGEX)) {
            throw new IllegalArgumentException("Password must contain only letters and digits!");
        }
    }
}
