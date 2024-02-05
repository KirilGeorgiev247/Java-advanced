package server.storage;

import server.data.group.Group;
import server.data.user.User;
import server.response.Response;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface Storage {
    public void addUser(String username, User user);
    public void addGroup(String name, Group group);
    public void addNotification(String username, String notification);
    public Map<String, User> getUsers();

    public Map<String, Group> getGroups();

    public Map<String, Queue<String>> getNotifications();

    public void saveData();
}
