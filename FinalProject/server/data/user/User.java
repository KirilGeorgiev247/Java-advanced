package server.data.user;

import server.data.group.Group;
import server.exception.AlreadyExists;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class User {

    private Map<String, User> friendList;
    private Map<String, Group> groups;

    private String username;

    private String hashedPass;
    public User(String username, String hashedPass) {
        friendList = new HashMap<>();
        groups = new HashMap<>();
        this.username = username;
        this.hashedPass = hashedPass;
    }

    public void addGroup(Group group) throws AlreadyExists {
        if(group == null) {
            throw new IllegalArgumentException("TODO");
        }

        if(groups.containsKey(group.name())) {
            throw new AlreadyExists("TODO");
        } else {
            groups.put(group.name(), group);
        }
    }
    public void addFriend(User user) throws AlreadyExists {
        if(user == null) {
            throw new IllegalArgumentException("TODO");
        }

        if(friendList.containsKey(user.getUsername())) {
            throw new AlreadyExists("TODO");
        } else {
            friendList.put(user.getUsername(), user);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPass() {
        return hashedPass;
    }
}
