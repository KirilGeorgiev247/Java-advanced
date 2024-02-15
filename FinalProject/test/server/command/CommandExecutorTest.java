package server.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.repository.UserRepository;
import server.response.Response;
import server.response.status.ServerStatusCode;
import server.session.Session;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandExecutorTest {

    private static final String RESPONSE_EQUALITY_CODE = "Response code is not as expected!";
    private static final String RESPONSE_EQUALITY_INFO = "Response info is not as expected!";
    @Mock
    private UserRepository mockRepo;
    @Mock
    private Session mockSession;
    private CommandExecutor executor;

    @BeforeEach
    public void setUp() {
        executor = new CommandExecutor(mockRepo);
    }

    // register
    @Test
    public void testRegisterWithValidArguments() {
        String[] args = {"user", "pass", "pass"};
        Command command = new Command("register", args);

        when(mockRepo.createUser(null, "user", "pass", "pass"))
            .thenReturn(Response.ok(ServerStatusCode.OK, "Registration successful"));

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Registration successful", response.info(), RESPONSE_EQUALITY_INFO);
        verify(mockSession).setUsername("user");
    }

    @Test
    public void testRegisterWithInvalidArgsCount() {
        String[] args = {"user", "pass"};
        Command command = new Command("register", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Register arguments are less or more! Example: register <username> <password> <rePassword>",
            response.info(), RESPONSE_EQUALITY_INFO);
    }

    @Test
    public void testRegisterWithInvalidArgs() {
        String[] args = {"user", "pass", null};
        Command command = new Command("register", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Payed group arguments are invalid! Example: register <username> <password> <rePassword>",
            response.info(), RESPONSE_EQUALITY_INFO);
    }

    // login
    @Test
    public void testLoginWithValidArguments() {
        String[] args = {"user", "pass"};
        Command command = new Command("login", args);

        when(mockRepo.loginUser(null, "user", "pass"))
            .thenReturn(Response.ok(ServerStatusCode.OK, "Successful login!"));

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Successful login!", response.info(), RESPONSE_EQUALITY_INFO);
        verify(mockSession).setUsername("user");
    }

    @Test
    public void testLoginWithInvalidArgsCount() {
        String[] args = {"user", "pass", "pass"};
        Command command = new Command("login", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Login arguments are less or more! Example: login <username> <password>", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    @Test
    public void testLoginWithInvalidArgs() {
        String[] args = {"user", null};
        Command command = new Command("login", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Login arguments are invalid! Example: login <username> <password>", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    // logout
    @Test
    public void testLogoutWithValidArguments() {
        when(mockSession.getUsername()).thenReturn("user");
        String[] args = {};
        Command command = new Command("logout", args);

        when(mockRepo.logOutUser("user"))
            .thenReturn(Response.ok(ServerStatusCode.OK, "Successful logout!"));

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Successful logout!", response.info(), RESPONSE_EQUALITY_INFO);
        verify(mockSession).setUsername(null);
    }

    @Test
    public void testLogoutWithInvalidArgsCount() {
        String[] args = {"user", "pass", "pass"};
        Command command = new Command("logout", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Logout command does not take any arguments! Example: logout", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    // add-friend
    @Test
    public void testAddFriendWithValidArguments() {
        String[] args = {"friend"};
        Command command = new Command("add-friend", args);
        when(mockSession.getUsername()).thenReturn("user");
        when(mockRepo.addFriend("user", "friend")).thenReturn(
            Response.ok(ServerStatusCode.OK, "Friend added successfully!"));

        Response response = executor.execute(command, mockSession);

        verify(mockRepo).addFriend("user", "friend");
        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Friend added successfully!", response.info(), RESPONSE_EQUALITY_INFO);
    }

    @Test
    public void testAddFriendWithInvalidArgsCount() {
        String[] args = {"user", "pass", "pass"};
        Command command = new Command("add-friend", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Add friend arguments are less or more! Example: add-friend <username>", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    @Test
    public void testAddFriendWithInvalidArgs() {
        String[] args = {null};
        Command command = new Command("add-friend", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Add friend argument is invalid! Example: add-friend <username>", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    // create-group
    @Test
    void testCreateGroupWithValidArguments() {
        String[] args = {"family", "user1", "user2"};
        Command command = new Command("create-group", args);

        when(mockSession.getUsername()).thenReturn("user");
        when(mockRepo.createGroup("user", "family", List.of("user1", "user2")))
            .thenReturn(Response.ok(ServerStatusCode.OK, "Group created successfully!"));

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Group created successfully!", response.info(), RESPONSE_EQUALITY_INFO);
    }

    @Test
    void testCreateGroupWithInvalidArgsCount() {
        String[] args = {"family"};
        Command command = new Command("create-group", args);
        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Create group arguments are less! Example: create-group <group_name> <username> [<username> ...]",
            response.info(), RESPONSE_EQUALITY_INFO);
    }

    @Test
    void testCreateGroupWithInsufficientMembersIncludingTheCreator() {
        String[] args = {"family", "user", "user2"};
        Command command = new Command("create-group", args);

        when(mockSession.getUsername()).thenReturn("user");

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Group should be created with at least 3 people including yourself!", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    @Test
    void testCreateGroupWithInsufficientMembersWithoutTheCreator() {
        String[] args = {"family", "user2"};
        Command command = new Command("create-group", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Group should be created with at least 3 people including yourself!", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    // split
    @Test
    public void testSplitWithValidArguments() {
        String[] args = {"50", "friend", "bira"};
        Command command = new Command("split", args);
        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        when(mockSession.getUsername()).thenReturn("user");
        when(mockRepo.split("user", "friend", new BigDecimal("50"), Optional.of(reason)))
            .thenReturn(Response.ok(ServerStatusCode.OK, "Split successful!"));

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Split successful!", response.info(), RESPONSE_EQUALITY_INFO);
        verify(mockRepo).split("user", "friend", new BigDecimal("50"), Optional.of(reason));
    }

    @Test
    public void testSplitWithInvalidAmountArgs() {
        String[] args = {"invalid", "friend", "bira"};
        Command command = new Command("split", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Split amount is invalid! Example: split <amount> <username> <reason_for_payment>",
            response.info(), RESPONSE_EQUALITY_INFO);
    }

    @Test
    public void testSplitWithInvalidArgsCount() {
        String[] args = {"50"};
        Command command = new Command("split", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Split arguments are less! Example: split <amount> <username> <reason_for_payment>",
            response.info(), RESPONSE_EQUALITY_INFO);
    }

    // split-group
    @Test
    public void testSplitGroupWithValidArguments() {
        String[] args = {"100", "bandata", "bira"};
        Command command = new Command("split-group", args);
        String reason = String.join(" ", "bira");

        when(mockSession.getUsername()).thenReturn("user");
        when(mockRepo.splitGroup("user", "bandata", new BigDecimal("100"), Optional.of(reason)))
            .thenReturn(Response.ok(ServerStatusCode.OK, "Split group successful!"));

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Split group successful!", response.info(), RESPONSE_EQUALITY_INFO);
        verify(mockRepo).splitGroup("user", "bandata", new BigDecimal("100"), Optional.of(reason));
    }

    @Test
    public void testSplitGroupWithInvalidAmount() {
        String[] args = {"invalid", "bandata", "bira"};
        Command command = new Command("split-group", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Split group amount is invalid! Example: split-group <amount> <group_name> <reason_for_payment>",
            response.info(), RESPONSE_EQUALITY_INFO);
    }

    @Test
    public void testSplitGroupWithInvalidArgsCount() {
        String[] args = {"100"};
        Command command = new Command("split-group", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Split group arguments are less! Example: split-group <amount> <group_name> <reason_for_payment>",
            response.info(), RESPONSE_EQUALITY_INFO);
    }

    // get-status
    @Test
    public void testGetStatusWithValidArguments() {
        String[] args = {};
        Command command = new Command("get-status", args);

        when(mockSession.getUsername()).thenReturn("user");
        when(mockRepo.getStatus("user"))
            .thenReturn(Response.ok(ServerStatusCode.OK, "info"));

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("info", response.info(), RESPONSE_EQUALITY_INFO);
        verify(mockRepo).getStatus("user");
    }

    @Test
    public void testGetStatusWithInvalidArgsCount() {
        String[] args = {"user", "pass", "pass"};
        Command command = new Command("get-status", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Get status command does not take any arguments! Example: get-status", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    // payed
    @Test
    public void testPayedWithValidArguments() {
        String[] args = {"100", "friend"};
        Command command = new Command("payed", args);

        when(mockSession.getUsername()).thenReturn("user");
        when(mockRepo.announcePayOff("user", new BigDecimal("100"), "friend"))
            .thenReturn(Response.ok(ServerStatusCode.OK, "Payment announced successfully!"));

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Payment announced successfully!", response.info(), RESPONSE_EQUALITY_INFO);
        verify(mockRepo).announcePayOff("user", new BigDecimal("100"), "friend");
    }

    @Test
    public void testPayedWithInvalidAmountArgs() {
        String[] args = {"invalid", "friend"};
        Command command = new Command("payed", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Pay amount is invalid! Example: payed <amount> <username>", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    @Test
    public void testPayedWithInvalidArgsCount() {
        String[] args = {"100"};
        Command command = new Command("payed", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Pay arguments are less or more! Example: payed <amount> <username>", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    // payment-history
    @Test
    public void testPaymentHistoryWithValidArguments() {
        String[] args = {};
        Command command = new Command("payment-history", args);

        when(mockSession.getUsername()).thenReturn("user");
        when(mockRepo.paymentHistory("user"))
            .thenReturn(Response.ok(ServerStatusCode.OK, "history"));

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("history", response.info(), RESPONSE_EQUALITY_INFO);
    }

    @Test
    public void testPaymentHistoryWithInvalidArgsCount() {
        String[] args = {"user"};
        Command command = new Command("payment-history", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Payment history command does not take any arguments! Example: payment-history", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    // help
    @Test
    public void testHelpWithValidArguments() {
        String[] args = {};
        Command command = new Command("help", args);

        Response response = executor.execute(command, null);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertTrue(response.info().startsWith("Available commands:"), RESPONSE_EQUALITY_INFO);
    }

    @Test
    public void testHelpWithInvalidArgsCount() {
        String[] args = {"extra"};
        Command command = new Command("help", args);

        Response response = executor.execute(command, null);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Help command does not take any arguments! Example: help", response.info(),
            RESPONSE_EQUALITY_INFO);
    }

    // disconnect
    @Test
    public void testDisconnectWithValidArguments() {
        String[] args = {};
        Command command = new Command("disconnect", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.OK.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Disconnected", response.info(), RESPONSE_EQUALITY_INFO);
    }

    @Test
    public void testDisconnectWithInvalidArgsCount() {
        String[] args = {"extra"};
        Command command = new Command("disconnect", args);

        Response response = executor.execute(command, mockSession);

        assertEquals(ServerStatusCode.BAD_REQUEST.getCode(), response.statusCode(), RESPONSE_EQUALITY_CODE);
        assertEquals("Disconnect command does not take any arguments! Example: disconnect", response.info(),
            RESPONSE_EQUALITY_INFO);
    }
}
