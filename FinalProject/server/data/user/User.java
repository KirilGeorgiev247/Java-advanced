package server.data.user;

import server.data.debt.Debt;
import server.data.group.Group;
import server.exception.AlreadyExistsException;
import server.exception.NotExistingUserException;
import server.exception.NotExistingRelationshipException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class User {

    private final Map<String, Debt> friendDebts;
    private final Map<String, List<Debt>> groupDebts;
    private final String username;
    private final String hashedPass;

    private boolean isLogged;
    public User(String username, String hashedPass) {
        friendDebts = new HashMap<>();
        groupDebts = new HashMap<>();
        this.username = username;
        this.hashedPass = hashedPass;
        isLogged = true;
    }

    public void addGroupDebt(Group group) throws AlreadyExistsException {
        if(group == null) {
            throw new IllegalArgumentException("TODO");
        }

        if(groupDebts.containsKey(group.name())) {
            throw new AlreadyExistsException("TODO");
        } else {
            List<Debt> debts = new ArrayList<>();
            for (User user :
                group.participants()) {
                if(!user.getUsername().equals(username)) {
                    Debt debt = new Debt(user.getUsername(), BigDecimal.ZERO);
                    debts.add(debt);
                }
            }

            groupDebts.put(group.name(), debts);
        }
    }
    public void addFriendDebt(User user) throws AlreadyExistsException {
        if(user == null) {
            throw new IllegalArgumentException("TODO");
        }

        if(friendDebts.containsKey(user.getUsername())) {
            throw new AlreadyExistsException("TODO");
        } else {
            Debt debt = new Debt(user.getUsername(), BigDecimal.ZERO);
            friendDebts.put(user.getUsername(), debt);
        }
    }

    public void updateFriendDebt(User user, BigDecimal amount, Optional<String> reason) throws NotExistingRelationshipException {
        if(friendDebts.containsKey(user.getUsername())) {
            friendDebts.get(user.getUsername()).update(amount);
            if(reason.isPresent() && friendDebts.get(user.getUsername()).getReason().isBlank()) {
                friendDebts.get(user.getUsername()).setReason(reason.get());
            }
        }

        throw new NotExistingRelationshipException("Relation with this user does not exist!");
    }

    public void updateGroupDebt(Group group, BigDecimal amount, Optional<String> reason) throws NotExistingUserException {
        for (Debt debt : groupDebts.get(group.name())) {
            debt.update(amount);
            if(reason.isPresent() && debt.getReason().isBlank()) {
                debt.setReason(reason.get());
            }
        }

        for (User user : group.participants()) {
            if(!user.getUsername().equals(username)) {
                for(Debt debt : user.getGroupDebts().get(group.name())) {
                    if(debt.getOwning().equals(username)) {
                        debt.update(amount);
                    }
                }
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPass() {
        return hashedPass;
    }

    public Map<String, Debt> getFriendDebts() {
        return friendDebts;
    }

    public Map<String, List<Debt>> getGroupDebts() {
        return groupDebts;
    }

    public boolean isLoggedIn() {
        return isLogged;
    }
    public void setLogIn() {
        isLogged = !isLogged;
    }
}
