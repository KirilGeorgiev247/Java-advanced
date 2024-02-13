package server.storage;

import server.data.group.Group;
import server.data.notification.Notification;
import server.data.user.User;
import server.exception.AlreadyExistsException;
import server.exception.NotExistingGroupExeption;
import server.exception.NotExistingUserException;

import java.util.Map;
import java.util.Queue;

public interface Storage {
    public User getUser(String username) throws NotExistingUserException;

    public Group getGroup(String name) throws NotExistingGroupExeption;

    public void addUser(User user) throws AlreadyExistsException;

    public void addGroup(Group group) throws AlreadyExistsException;

    public Map<String, User> getUsers();

    public Map<String, Group> getGroups();

    public void addPaymentActionToHistory(User user, Notification notification);

    public Queue<Notification> getUserPaymentHistory(User user);

    public void save();
}
