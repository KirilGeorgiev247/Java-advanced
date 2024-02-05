package server.storage;

import server.data.group.Group;
import server.data.user.User;

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
    public void addUser(String username, User user) {
        users.put(username, user);
    }

    @Override
    public void addGroup(String name, Group group) {
        groups.put(name, group);
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
