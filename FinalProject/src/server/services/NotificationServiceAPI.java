package server.services;

import server.data.group.Group;
import server.data.notification.NotificationType;
import server.data.user.User;

import java.math.BigDecimal;

public interface NotificationServiceAPI {
    public void notifyAddFriend(User currUser, User friendUser);

    public void notifyPayOff(String clientUsername, User friendUser, BigDecimal amount);

    public void notifySplit(User currUser, User friendUser, BigDecimal splitAmount);

    public void notifyGroupSplit(User currUser, Group group, BigDecimal splitAmount);

    public void notifyGroupCreation(User currUser, Group group);
}
