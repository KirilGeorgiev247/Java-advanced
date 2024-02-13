package server.data.user;

import server.data.debt.Debt;
import server.data.debt.DebtType;
import server.data.notification.Notification;
import server.exception.AlreadyExistsException;
import server.exception.NotExistingRelationshipException;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class User {
    private final Map<String, Debt> debts;
    private final Queue<Notification> notifications;
    private final String username;
    private final String hashedPass;

    public User(String username, String hashedPass) {
        debts = new HashMap<>();
        notifications = new ArrayDeque<>();
        this.username = username;
        this.hashedPass = hashedPass;
    }

    public void addDebt(User user, DebtType type, Optional<String> groupName) throws AlreadyExistsException {
        if (user == null) {
            throw new IllegalArgumentException("That is not a valid username!");
        }

        if (debts.containsKey(user.getUsername())) {
            throw new AlreadyExistsException("You are already friends!");
        } else {
            Debt debt;
            debt = groupName.map(s -> new Debt(user.getUsername(), BigDecimal.ZERO, type, s))
                .orElseGet(() -> new Debt(user.getUsername(), BigDecimal.ZERO, type));
            debts.put(user.getUsername(), debt);
        }
    }

    public void updateDebt(User user, BigDecimal amount, Optional<String> reason)
        throws NotExistingRelationshipException {
        if (debts.containsKey(user.getUsername())) {
            debts.get(user.getUsername()).update(amount);
            if (reason.isPresent() && !debts.get(user.getUsername()).getReasons().contains(reason.get())) {
                debts.get(user.getUsername()).addReason(reason.get());
            }
        } else {
            throw new NotExistingRelationshipException("Relation with this user does not exist!");
        }
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public Queue<Notification> getNotifications() {
        return notifications;
    }

    public void clearNotifications() {
        notifications.clear();
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPass() {
        return hashedPass;
    }

    public Map<String, Debt> getDebts() {
        return debts;
    }
}
