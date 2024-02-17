package server.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.algorithm.HashCalculator;
import server.data.debt.Debt;
import server.data.debt.DebtType;
import server.data.group.Group;
import server.data.notification.Notification;
import server.data.notification.NotificationType;
import server.data.user.User;
import server.exception.AlreadyExistsException;
import server.exception.NotExistingGroupExeption;
import server.exception.NotExistingRelationshipException;
import server.exception.NotExistingUserException;
import server.repository.MJTUserRepository;
import server.response.Response;
import server.response.status.ServerStatusCode;
import server.services.NotificationService;
import server.storage.Storage;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MJTUserRepositoryTest {

    private static final String CLIENT_USERNAME = "user";
    private static final String FRIEND_USERNAME = "friend";
    private static final BigDecimal AMOUNT = new BigDecimal("100");
    private static final String REASON = "bira";
    private static final String GROUP_NAME = "bandata";
    private static final String PASSWORD = "gosho123";
    @Mock
    private Storage storage;
    @Mock
    private HashCalculator hashCalculator;
    @Mock
    private NotificationService notificationService;

    @Mock
    private User clientUser;

    @Mock
    private User friendUser;

    @Mock
    private Group group;

    private MJTUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = new MJTUserRepository(storage, hashCalculator, notificationService);
    }

    // add friend
    @Test
    public void testAddFriendSuccess() throws NotExistingUserException, AlreadyExistsException {
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(storage.getUser(FRIEND_USERNAME)).thenReturn(friendUser);

        Response response = userRepository.addFriend(CLIENT_USERNAME, FRIEND_USERNAME);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), "Adding friend should return OK status!");
        verify(storage).save();
        verify(clientUser).addDebt(friendUser, DebtType.FRIEND, Optional.empty());
        verify(friendUser).addDebt(clientUser, DebtType.FRIEND, Optional.empty());
        verify(notificationService).notifyAddFriend(clientUser, friendUser);
    }

    @Test
    public void testAddFriendWhenNotAuthorized() {
        Response response = userRepository.addFriend(null, FRIEND_USERNAME);

        assertEquals(ServerStatusCode.UNAUTHORIZED.getCode(), response.statusCode(),
            "Unauthorized access should return UNAUTHORIZED status!");
        assertEquals("You are not logged in!", response.info(),
            "Unauthorized access should return proper info message!");
    }

    @Test
    public void testAddFriendNotExistingUserException() throws Exception {
        when(storage.getUser(CLIENT_USERNAME)).thenThrow(new NotExistingUserException("User does not exist"));

        Response response = userRepository.addFriend(CLIENT_USERNAME, FRIEND_USERNAME);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Adding friend with non-existing user should return BAD_REQUEST status!");
        assertEquals("User does not exist", response.info(),
            "Adding friend with non-existing user should return proper info message!");
    }

    @Test
    public void testAddFriendAlreadyExistsException() throws Exception {
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(storage.getUser(FRIEND_USERNAME)).thenReturn(friendUser);
        doThrow(new AlreadyExistsException("Friendship already exists")).when(clientUser)
            .addDebt(friendUser, DebtType.FRIEND, Optional.empty());

        Response response = userRepository.addFriend(CLIENT_USERNAME, FRIEND_USERNAME);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Adding existing friend should return BAD_REQUEST status!");
        assertEquals("Friendship already exists", response.info(),
            "Adding existing friend should return proper info message!");
    }

    // announce pay off
    @Test
    public void testAnnouncePayOffSuccess() throws NotExistingUserException, NotExistingRelationshipException {
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(storage.getUser(FRIEND_USERNAME)).thenReturn(friendUser);
        when(clientUser.getDebts()).thenReturn(
            Map.of(FRIEND_USERNAME, new Debt(FRIEND_USERNAME, BigDecimal.ZERO, DebtType.FRIEND)));

        Response response = userRepository.announcePayOff(CLIENT_USERNAME, AMOUNT, FRIEND_USERNAME);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(),
            "Announcing pay off should return OK status!");
        verify(friendUser).updateDebt(clientUser, AMOUNT.negate(), Optional.empty());
        verify(clientUser).updateDebt(friendUser, AMOUNT, Optional.empty());
        verify(notificationService).notifyPayOff(CLIENT_USERNAME, friendUser, AMOUNT);
        verify(storage).save();
    }

    @Test
    public void testAnnouncePayOffNotExistingUserException() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenThrow(new NotExistingUserException("User does not exist"));

        Response response = userRepository.announcePayOff(CLIENT_USERNAME, BigDecimal.TEN, FRIEND_USERNAME);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Announcing pay off with non-existing user should return BAD_REQUEST status!");
        assertEquals("User does not exist", response.info(),
            "Announcing pay off with non-existing user should return proper info message!");
    }

    @Test
    public void testAnnouncePayOffNotExistingRelationshipException() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(clientUser.getDebts()).thenReturn(Map.of());

        Response response = userRepository.announcePayOff(CLIENT_USERNAME, AMOUNT, FRIEND_USERNAME);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Announcing pay off with non-existing relationship should return BAD_REQUEST status!");
        assertEquals("You are not in a relationship with this user!", response.info(),
            "Announcing pay off with non-existing relationship should return proper info message!");
    }

    @Test
    public void testAnnouncePayOffWhenNotUnauthorized() {
        Response response = userRepository.announcePayOff(null, BigDecimal.TEN, FRIEND_USERNAME);

        assertEquals(ServerStatusCode.UNAUTHORIZED.getCode(), response.statusCode(),
            "Unauthorized access should return UNAUTHORIZED status!");
        assertEquals("You are not logged in!", response.info(),
            "Unauthorized access should return proper info message!");
    }

    // get status
    @Test
    public void testGetStatusSuccessNoDebts() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        Map<String, Debt> mockedDebts = new HashMap<>();
        when(clientUser.getDebts()).thenReturn(mockedDebts);

        Response response = userRepository.getStatus(CLIENT_USERNAME);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(),
            "Getting status with no debts should return OK status!");
        assertEquals("You have no debts!", response.info(),
            "Getting status with no debts should return proper info message!");
    }

    @Test
    public void testGetStatusWithFriendDebts() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        Map<String, Debt> mockedDebts = new HashMap<>();
        Debt mockDebt = mock(Debt.class);
        when(mockDebt.getAmount()).thenReturn(AMOUNT);
        when(mockDebt.getReasons()).thenReturn(List.of(REASON));
        when(mockDebt.getType()).thenReturn(DebtType.FRIEND);
        mockedDebts.put(FRIEND_USERNAME, mockDebt);
        when(clientUser.getDebts()).thenReturn(mockedDebts);

        Response response = userRepository.getStatus(CLIENT_USERNAME);

        String expectedInfo =
            "Friends: " + System.lineSeparator() + "* friend owes you " + AMOUNT + " [" + REASON + "]";
        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(),
            "Getting status with friend debts should return OK status!");
        assertEquals(expectedInfo, response.info(),
            "Getting status with friend debts should return proper info message!");
    }

    @Test
    public void testGetStatusWithGroupDebts() throws NotExistingUserException, NotExistingGroupExeption {
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        Map<String, Debt> mockedDebts = new HashMap<>();

        Debt mockDebt = mock(Debt.class);
        when(mockDebt.getAmount()).thenReturn(AMOUNT);
        when(mockDebt.getReasons()).thenReturn(List.of(REASON));
        when(mockDebt.getType()).thenReturn(DebtType.GROUP);
        when(mockDebt.getOwning()).thenReturn(FRIEND_USERNAME);
        when(mockDebt.getGroupName()).thenReturn(GROUP_NAME);
        mockedDebts.put(FRIEND_USERNAME, mockDebt);
        when(clientUser.getDebts()).thenReturn(mockedDebts);

        Response response = userRepository.getStatus(CLIENT_USERNAME);

        String expectedInfo =
            "Groups: " + System.lineSeparator() + "* " + GROUP_NAME + ":" + System.lineSeparator() + "  " +
                FRIEND_USERNAME +
                " owes you " + AMOUNT + " LV [" + REASON + "].";
        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(),
            "Getting status with group debts should return OK status!");
        assertEquals(expectedInfo, response.info(),
            "Getting status with group debts should return proper info message!");
    }

    @Test
    public void testGetStatusNotExistingUser() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenThrow(new NotExistingUserException("User does not exist"));

        Response response = userRepository.getStatus(CLIENT_USERNAME);

        assertEquals(ServerStatusCode.NOT_FOUND.getCode(), response.statusCode(),
            "Getting status for non-existing user should return NOT_FOUND status!");
        assertEquals("User with such username does not exist!", response.info(),
            "Getting status for non-existing user should return proper info message!");
    }

    @Test
    public void testGetStatusWhenNotUnauthorized() {
        Response response = userRepository.getStatus(null);

        assertEquals(ServerStatusCode.UNAUTHORIZED.getCode(), response.statusCode(),
            "Unauthorized access should return UNAUTHORIZED status!");
        assertEquals("You are not logged in!", response.info(),
            "Unauthorized access should return proper info message!");
    }

    // split
    @Test
    public void testSplitSuccessWithDebtUpdates() throws Exception {
        Debt clientOwesFriend = new Debt(FRIEND_USERNAME, BigDecimal.ZERO, DebtType.FRIEND);
        Map<String, Debt> clientDebts = new HashMap<>();
        clientDebts.put(FRIEND_USERNAME, clientOwesFriend);

        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(storage.getUser(FRIEND_USERNAME)).thenReturn(friendUser);
        when(clientUser.getDebts()).thenReturn(clientDebts);
        when(friendUser.getUsername()).thenReturn(FRIEND_USERNAME);

        Response splitResponse = userRepository.split(CLIENT_USERNAME, FRIEND_USERNAME, AMOUNT, Optional.of(REASON));

        assertEquals(ServerStatusCode.OK.getCode(), splitResponse.statusCode(),
            "Splitting with debt updates should return OK status!");
        assertEquals("Debts updated successfully!", splitResponse.info(),
            "Splitting with debt updates should return proper info message!");

        BigDecimal expectedAmountEach = AMOUNT.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        verify(friendUser).updateDebt(clientUser, expectedAmountEach, Optional.of(REASON));
        verify(clientUser).updateDebt(friendUser, expectedAmountEach.negate(), Optional.of(REASON));

        verify(storage).save();
        verify(notificationService).notifySplit(clientUser, friendUser, expectedAmountEach);
    }

    @Test
    public void testSplitNotExistingUserException() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenThrow(new NotExistingUserException("User does not exist"));

        Response response = userRepository.split(CLIENT_USERNAME, FRIEND_USERNAME, AMOUNT, Optional.of(REASON));

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Splitting with non-existing user should return BAD_REQUEST status!");
        assertEquals("User does not exist", response.info(),
            "Splitting with non-existing user should return proper info message!");
    }

    @Test
    public void testSplitNotExistingRelationshipException() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(storage.getUser(FRIEND_USERNAME)).thenReturn(friendUser);
        when(clientUser.getDebts()).thenReturn(new HashMap<>());

        Response response = userRepository.split(CLIENT_USERNAME, FRIEND_USERNAME, AMOUNT, Optional.of(REASON));

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Splitting with non-existing relationship should return BAD_REQUEST status!");
        assertEquals("You have no relationship with this user!", response.info(),
            "Splitting with non-existing relationship should return proper info message!");
    }

    @Test
    public void testSplitUnauthorized() {
        Response response = userRepository.split(null, FRIEND_USERNAME, AMOUNT, Optional.of(REASON));

        assertEquals(ServerStatusCode.UNAUTHORIZED.getCode(), response.statusCode(),
            "Unauthorized access should return UNAUTHORIZED status!");
        assertEquals("You are not logged in!", response.info(),
            "Unauthorized access should return proper info message!");
    }

    // split group
    @Test
    public void testSplitGroupSuccess()
        throws NotExistingUserException, NotExistingGroupExeption, NotExistingRelationshipException {

        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(clientUser.getUsername()).thenReturn(CLIENT_USERNAME);
        when(storage.getGroup(GROUP_NAME)).thenReturn(group);
        when(group.groupCreator()).thenReturn(CLIENT_USERNAME);
        when(group.participants()).thenReturn(List.of(clientUser));

        Response response = userRepository.splitGroup(CLIENT_USERNAME, GROUP_NAME, AMOUNT, Optional.of(REASON));

        BigDecimal expectedSplitAmount =
            AMOUNT.divide(new BigDecimal(group.participants().size()), 2, RoundingMode.HALF_UP);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), "Splitting group should return OK status!");
        assertEquals("Debts updated successfully!", response.info(),
            "Splitting group should return proper info message!");

        verify(notificationService).notifyGroupSplit(clientUser, group, expectedSplitAmount);
        verify(storage).save();
    }

    @Test
    public void testSplitGroupNotExistingGroupException() throws NotExistingGroupExeption {
        when(storage.getGroup(GROUP_NAME)).thenThrow(new NotExistingGroupExeption("Group does not exists!"));

        Response response = userRepository.splitGroup(CLIENT_USERNAME, GROUP_NAME, AMOUNT, Optional.of(REASON));

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Splitting non-existing group should return BAD_REQUEST status!");
        assertEquals("Group does not exists!", response.info(),
            "Splitting non-existing group should return proper info message!");
    }

    @Test
    public void testSplitGroupUnauthorized() {
        Response response = userRepository.splitGroup(null, GROUP_NAME, AMOUNT, Optional.of(REASON));

        assertEquals(ServerStatusCode.UNAUTHORIZED.getCode(), response.statusCode(),
            "Unauthorized access should return UNAUTHORIZED status!");
        assertEquals("You are not logged in!", response.info(),
            "Unauthorized access should return proper info message!");
    }

    // create group
    @Test
    public void testCreateGroupSuccess() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(storage.getUser(FRIEND_USERNAME)).thenReturn(friendUser);
        when(clientUser.getUsername()).thenReturn(CLIENT_USERNAME);
        when(friendUser.getUsername()).thenReturn(FRIEND_USERNAME);

        Response response =
            userRepository.createGroup(CLIENT_USERNAME, GROUP_NAME, List.of(CLIENT_USERNAME, FRIEND_USERNAME));

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), "Creating group should return OK status!");
        assertEquals("Group " + GROUP_NAME + " created!", response.info(),
            "Creating group should return proper info message!");

        verify(storage).save();
    }

    @Test
    public void testCreateGroupAlreadyExistsException()
        throws NotExistingUserException, AlreadyExistsException {
        when(clientUser.getUsername()).thenReturn(CLIENT_USERNAME);
        when(friendUser.getUsername()).thenReturn(FRIEND_USERNAME);
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(storage.getUser(FRIEND_USERNAME)).thenReturn(friendUser);

        List<String> participants = List.of(CLIENT_USERNAME, FRIEND_USERNAME);

        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        doThrow(new AlreadyExistsException("Group with such name already exist!"))
            .when(storage).addGroup(any(Group.class));

        Response response = userRepository.createGroup(CLIENT_USERNAME, GROUP_NAME, participants);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Creating group with existing name should return BAD_REQUEST status!");
        assertTrue(response.info().contains("Group with such name already exist!"),
            "Creating group with existing name should return proper info message!");
    }

    @Test
    public void testCreateGroupNotExistingUserException() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenThrow(new NotExistingUserException("User does not exist"));

        Response response =
            userRepository.createGroup(CLIENT_USERNAME, GROUP_NAME, List.of(CLIENT_USERNAME, FRIEND_USERNAME));

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Creating group with non-existing user should return BAD_REQUEST status!");
        assertEquals("User does not exist", response.info(),
            "Creating group with non-existing user should return proper info message!");
    }

    @Test
    public void testCreateGroupUnauthorized() {
        Response response = userRepository.createGroup(null, GROUP_NAME, List.of(CLIENT_USERNAME, FRIEND_USERNAME));

        assertEquals(ServerStatusCode.UNAUTHORIZED.getCode(), response.statusCode(),
            "Unauthorized access should return UNAUTHORIZED status!");
        assertEquals("You are not logged in!", response.info(),
            "Unauthorized access should return proper info message!");
    }

    // create user
    @Test
    public void testCreateUserSuccess() throws NoSuchAlgorithmException, IOException {
        when(hashCalculator.calculate(any(InputStream.class))).thenReturn("hashedPassword");

        Response response = userRepository.createUser(null, CLIENT_USERNAME, PASSWORD, PASSWORD);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), "Creating user should return OK status!");
        assertEquals("Successful registration!", response.info(), "Creating user should return proper info message!");
    }

    @Test
    public void testCreateUserAlreadyExistsException() throws AlreadyExistsException {
        doThrow(new AlreadyExistsException("User with such username already exist!"))
            .when(storage).addUser(any(User.class));

        Response response = userRepository.createUser(null, CLIENT_USERNAME, PASSWORD, PASSWORD);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Creating user with existing username should return BAD_REQUEST status!");
        assertTrue(response.info().contains("User with such username already exist!"),
            "Creating user with existing username should return proper info message!");
    }

    @Test
    public void testCreateUserAlreadyAuthorized() {
        Response response = userRepository.createUser(CLIENT_USERNAME, CLIENT_USERNAME, PASSWORD, PASSWORD);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Creating user when already authorized should return BAD_REQUEST status!");
        assertEquals("You are currently logged!", response.info(),
            "Creating user when already authorized should return proper info message!");
    }

    @Test
    public void testCreateUserInvalidPasswordLength() {
        String password = "short";
        String rePassword = "short";

        Response response = userRepository.createUser(null, CLIENT_USERNAME, password, rePassword);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Creating user with invalid password length should return BAD_REQUEST status!");
        assertTrue(response.info().contains("Password should be between"),
            "Creating user with invalid password length should return proper info message!");
    }

    @Test
    public void testCreateUserPasswordsNotMatching() {
        String rePassword = "misho123";

        Response response = userRepository.createUser(null, CLIENT_USERNAME, PASSWORD, rePassword);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Creating user with passwords not matching should return BAD_REQUEST status!");
        assertEquals("Password and rePassword must match!", response.info(),
            "Creating user with passwords not matching should return proper info message!");
    }

    @Test
    public void testCreateUserPasswordNotValidSymbols() {
        String password = "gosho@@123";
        String rePassword = "gosho@@123";

        Response response = userRepository.createUser(null, CLIENT_USERNAME, password, rePassword);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Creating user with password containing invalid symbols should return BAD_REQUEST status!");
        assertEquals("Password must contain only letters and digits!", response.info(),
            "Creating user with password containing invalid symbols should return proper info message!");
    }

    @Test
    public void testCreateUserHashingThrows() throws NoSuchAlgorithmException, IOException {
        when(hashCalculator.calculate(any(InputStream.class)))
            .thenThrow(new NoSuchAlgorithmException("Server error"));

        Response response = userRepository.createUser(null, CLIENT_USERNAME, PASSWORD, PASSWORD);

        assertEquals(ServerStatusCode.INTERNAL_SERVER_ERROR.getCode(), response.statusCode(),
            "Creating user with hashing error should return INTERNAL_SERVER_ERROR status!");
        assertEquals("Server error", response.info(),
            "Creating user with hashing error should return proper info message!");
    }

    // login
    @Test
    public void testLoginUserSuccessNoNotifications()
        throws NotExistingUserException, NoSuchAlgorithmException, IOException {

        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(hashCalculator.calculate(any(InputStream.class))).thenReturn("hashedPassword");
        when(clientUser.getHashedPass()).thenReturn("hashedPassword");
        when(clientUser.getNotifications()).thenReturn(new ArrayDeque<>());

        Response response = userRepository.loginUser(null, CLIENT_USERNAME, PASSWORD);

        String expectedInfo =
            "Successful login!" + System.lineSeparator() + "**Notifications**" + System.lineSeparator() +
                "No notifications to show.";

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(),
            "Logging in with no notifications should return OK status!");
        assertEquals(expectedInfo, response.info(),
            "Logging in with no notifications should return proper info message!");

        verify(clientUser).clearNotifications();
    }

    @Test
    public void testLoginUserSuccessWithNotifications()
        throws NoSuchAlgorithmException, IOException, NotExistingUserException {
        String hashedPassword = "hashedPassword";
        User mockUser = mock(User.class);
        Queue<Notification> notifications = new ArrayDeque<>();
        notifications.add(new Notification(NotificationType.FRIEND, "Friend notification"));
        notifications.add(new Notification(NotificationType.GROUP, "Group notification"));

        when(storage.getUser(CLIENT_USERNAME)).thenReturn(mockUser);
        when(hashCalculator.calculate(any(InputStream.class))).thenReturn(hashedPassword);
        when(mockUser.getHashedPass()).thenReturn(hashedPassword);
        when(mockUser.getNotifications()).thenReturn(notifications);

        Response response = userRepository.loginUser(null, CLIENT_USERNAME, PASSWORD);

        String expectedInfo = "Successful login!" + System.lineSeparator() +
            "**Notifications**" + System.lineSeparator() +
            "Friends:" + System.lineSeparator() +
            "Friend notification" + System.lineSeparator() +
            "Groups:" + System.lineSeparator() +
            "Group notification";

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(),
            "Logging in with notifications should return OK status!");
        assertEquals(expectedInfo, response.info(), "Logging in with notifications should return proper info message!");

        verify(mockUser).clearNotifications();
    }

    @Test
    public void testLoginUserAlreadyAuthorized() {
        Response response = userRepository.loginUser(CLIENT_USERNAME, CLIENT_USERNAME, PASSWORD);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Logging in when already authorized should return BAD_REQUEST status!");
        assertEquals("You are currently logged!", response.info(),
            "Logging in when already authorized should return proper info message!");
    }

    @Test
    public void testLoginUserInvalidPassword() throws NotExistingUserException, NoSuchAlgorithmException, IOException {
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(hashCalculator.calculate(any(InputStream.class))).thenReturn("wrongHashedPassword");
        when(clientUser.getHashedPass()).thenReturn("correctHashedPassword");

        Response response = userRepository.loginUser(null, CLIENT_USERNAME, PASSWORD);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Logging in with invalid password should return BAD_REQUEST status!");
        assertEquals("Invalid password!", response.info(),
            "Logging in with invalid password should return proper info message!");
    }

    @Test
    public void testLoginUserHashingThrows() throws NoSuchAlgorithmException, IOException {
        when(hashCalculator.calculate(any(InputStream.class))).thenThrow(new NoSuchAlgorithmException("Server error"));

        Response response = userRepository.loginUser(null, CLIENT_USERNAME, PASSWORD);

        assertEquals(ServerStatusCode.INTERNAL_SERVER_ERROR.getCode(), response.statusCode(),
            "Logging in with hashing error should return INTERNAL_SERVER_ERROR status!");
        assertEquals("Server error", response.info(),
            "Logging in with hashing error should return proper info message!");
    }

    @Test
    public void testLoginUserNotExistingException() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenThrow(new NotExistingUserException("User does not exist"));

        Response response = userRepository.loginUser(null, CLIENT_USERNAME, PASSWORD);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Logging in with non-existing user should return BAD_REQUEST status!");
        assertEquals("User does not exist", response.info(),
            "Logging in with non-existing user should return proper info message!");
    }

    // logout
    @Test
    public void testLogOutUserSuccess() {
        Response response = userRepository.logOutUser(CLIENT_USERNAME);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), "Logging out should return OK status!");
        assertEquals("Successful logout!", response.info(), "Logging out should return proper info message!");
    }

    @Test
    public void testLogOutUserNotAuthorized() {
        Response response = userRepository.logOutUser(null);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(),
            "Logging out when not authorized should return BAD_REQUEST status!");
        assertEquals("You are not logged!", response.info(),
            "Logging out when not authorized should return proper info message!");
    }

    // payment history
    @Test
    public void testPaymentHistorySuccessNoPayments() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(storage.getUserPaymentHistory(clientUser)).thenReturn(new ArrayDeque<>());

        Response response = userRepository.paymentHistory(CLIENT_USERNAME);

        String expectedPaymentHistory = "Payment actions history:" + System.lineSeparator() + "No payment actions.";

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(),
            "Retrieving payment history with no payments should return OK status!");
        assertEquals(expectedPaymentHistory, response.info(),
            "Retrieving payment history with no payments should return proper info message!");
    }

    @Test
    public void testPaymentHistorySuccessWithPayments() throws NotExistingUserException {
        Queue<Notification> paymentHistory = new ArrayDeque<>();
        paymentHistory.add(new Notification(NotificationType.FRIEND, "Payment"));
        when(storage.getUser(CLIENT_USERNAME)).thenReturn(clientUser);
        when(storage.getUserPaymentHistory(clientUser)).thenReturn(paymentHistory);

        Response response = userRepository.paymentHistory(CLIENT_USERNAME);
        String expectedPaymentHistory = "Payment actions history:" + System.lineSeparator() + "Payment";

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(),
            "Retrieving payment history with payments should return OK status!");
        assertEquals(expectedPaymentHistory, response.info(),
            "Retrieving payment history with payments should return proper info message!");
    }

    @Test
    public void testPaymentHistoryNotAuthorized() {
        Response response = userRepository.paymentHistory(null);

        assertEquals(ServerStatusCode.UNAUTHORIZED.getCode(), response.statusCode(),
            "Retrieving payment history without authorization should return UNAUTHORIZED status!");
        assertEquals("You are not logged in!", response.info(),
            "Retrieving payment history without authorization should return proper info message!");
    }

    @Test
    public void testPaymentHistoryNotExistingException() throws NotExistingUserException {
        when(storage.getUser(CLIENT_USERNAME)).thenThrow(new NotExistingUserException("User does not exist"));

        Response response = userRepository.paymentHistory(CLIENT_USERNAME);

        assertEquals(ServerStatusCode.NOT_FOUND.getCode(), response.statusCode(),
            "Retrieving payment history for non-existing user should return NOT_FOUND status!");
        assertEquals("User with such username does not exist!", response.info(),
            "Retrieving payment history for non-existing user should return proper info message!");
    }
}