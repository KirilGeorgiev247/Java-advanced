package server.services;

import server.data.debt.Debt;
import server.data.group.Group;
import server.data.notification.Notification;
import server.data.notification.NotificationType;
import server.data.user.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class NotificationService implements NotificationServiceAPI {
    @Override
    public void notifyAddFriend(User currUser, User friendUser) {
        friendUser.addNotification(new Notification(NotificationType.FRIEND,
            currUser.getUsername() + " added you as a friend."));
    }

    @Override
    public void notifyPayOff(String clientUsername, User friendUser, BigDecimal amount) {
        Debt currDebt = friendUser.getDebts().get(clientUsername);

        NotificationType notificationType;
        if (currDebt.getGroupName() != null && !currDebt.getGroupName().isBlank()) {
            notificationType = NotificationType.GROUP;
        } else {
            notificationType = NotificationType.FRIEND;
        }

        friendUser.addNotification(new Notification(notificationType,
            clientUsername + " approved your payment for " + amount + " LV " +
                getStringifiedReasons(currDebt.getReasons()) + "."));
    }

    @Override
    public void notifySplit(User currUser, User friendUser, BigDecimal splitAmount) {
        Debt currDebt = currUser.getDebts().get(friendUser.getUsername());

        friendUser.addNotification(new Notification(NotificationType.FRIEND,
            "You received a split request from " + currUser.getUsername() + " for " + splitAmount + " LV " +
                getStringifiedReasons(currDebt.getReasons()) + "."));
    }

    @Override
    public void notifyGroupSplit(User currUser, Group group, BigDecimal splitAmount) {
        Optional<User>
            participant =
            group.participants().stream().filter(u -> !u.getUsername().equals(currUser.getUsername())).findFirst();

        if (participant.isPresent()) {
            Debt currDebt = currUser.getDebts().get(participant.get().getUsername());

            group.addNotification(new Notification(NotificationType.GROUP,
                    "You received a split request from " + currUser.getUsername() + " in group " + group.name() + " for " +
                        splitAmount + " LV " + getStringifiedReasons(currDebt.getReasons()) + "."),
                currUser.getUsername());
        }
    }

    @Override
    public void notifyGroupCreation(User currUser, Group group) {
        group.addNotification(new Notification(NotificationType.GROUP,
            currUser.getUsername() + " created a group " + group.name() + "."), currUser.getUsername());
    }

    private String getStringifiedReasons(List<String> reasons) {
        StringBuilder sb = new StringBuilder();

        sb.append("[ ");

        for (int i = 0; i < reasons.size(); i++) {
            if (!reasons.get(i).isBlank()) {
                sb.append(reasons.get(i));
                if (i != reasons.size() - 1) {
                    sb.append(", ");
                }
            }
        }

        sb.append(" ]");

        return sb.toString();
    }
}
