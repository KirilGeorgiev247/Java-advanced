package server.storage;

import server.data.group.Group;
import server.data.user.User;
import server.exception.AlreadyExistsException;
import server.exception.NotExistingGroupExeption;
import server.exception.NotExistingUserException;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class FileStorage implements Storage {

    private final Map<String, User> users = new HashMap();

    private final Map<String, Group> groups = new HashMap<>();

    private final Map<String, Queue<String>> notifications = new HashMap<>();

    public FileStorage() {
        loadData();
    }

    @Override
    public User getUser(String username) throws NotExistingUserException {
        if (users.containsKey(username)) {
            return users.get(username);
        }

        throw new NotExistingUserException("User with such username does not exist!");
    }

    @Override
    public Group getGroup(String name) throws NotExistingGroupExeption {
        if(groups.containsKey(name)) {
            return groups.get(name);
        }

        throw new NotExistingGroupExeption("Group with such name does not exist!");
    }

    @Override
    public void addUser(User user) throws AlreadyExistsException {
        if (users.containsKey(user.getUsername())) {
            throw new AlreadyExistsException("User with such username already exist!");
        }

        users.put(user.getUsername(), user);
    }

    @Override
    public void addGroup(Group group) throws AlreadyExistsException {
        if (groups.containsKey(group.name())) {
            throw new AlreadyExistsException("Group with such name already exist!");
        }

        groups.put(group.name(), group);
    }

    @Override
    public void addNotification(String username, String notification) {
        if(notifications.containsKey(username)) {
            notifications.get(username).add(notification);
        } else {
            Queue<String> notificationsQueue = new ArrayDeque<>();
            notificationsQueue.add(notification);
            notifications.put(username, notificationsQueue);
        }
    }

    @Override
    public Map<String, User> getUsers() {
        return users;
    }

    @Override
    public Map<String, Group> getGroups() {
        return groups;
    }

    @Override
    public Map<String, Queue<String>> getNotifications() {
        return notifications;
    }

    @Override
    public void saveData() {

    }

    private void loadData() {

    }
}
