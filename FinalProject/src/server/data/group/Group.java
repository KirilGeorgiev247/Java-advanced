package server.data.group;

import server.data.notification.Notification;
import server.data.user.User;

import java.util.List;

public record Group(String name, String groupCreator, List<User> participants) {

    public void addNotification(Notification notification, String sender) {
        for (User user : participants) {
            if (!user.getUsername().equals(sender)) {
                user.addNotification(notification);
            }
        }
    }
}
