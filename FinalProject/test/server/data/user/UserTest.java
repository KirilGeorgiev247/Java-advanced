package server.data.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.data.debt.DebtType;
import server.exception.AlreadyExistsException;
import server.exception.NotExistingRelationshipException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {
    private static final String CLIENT_USERNAME = "user";
    private static final String FRIEND_USERNAME = "friend";
    private static final BigDecimal AMOUNT = new BigDecimal("100");
    private static final String REASON = "bira";
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User(CLIENT_USERNAME, "hashedPass1");
        user2 = new User(FRIEND_USERNAME, "hashedPass2");
    }

    @Test
    void testAddDebtSuccess() throws AlreadyExistsException {
        user1.addDebt(user2, DebtType.FRIEND, Optional.empty());
        assertTrue(user1.getDebts().containsKey(user2.getUsername()), "Debt with other user should have been added!");
    }

    @Test
    void testAddDebtAlreadyExistsException() {
        assertThrows(AlreadyExistsException.class, () -> {
            user1.addDebt(user2, DebtType.FRIEND, Optional.empty());
            user1.addDebt(user2, DebtType.FRIEND, Optional.empty());
        }, "Already exists exception should be thrown when existing debt is added twice!");
    }

    @Test
    void testUpdateDebtSuccess() throws AlreadyExistsException, NotExistingRelationshipException {
        user1.addDebt(user2, DebtType.FRIEND, Optional.empty());
        user1.updateDebt(user2, AMOUNT, Optional.of(REASON));
        assertEquals(0, AMOUNT.negate().compareTo(user1.getDebts().get(user2.getUsername()).getAmount()),
            "Debt should have been updated correctly!");
        assertTrue(user1.getDebts().get(user2.getUsername()).getReasons().contains(REASON),
            "Debt reason should have been updated correctly!");
    }

    @Test
    void testUpdateDebtNotExistingRelationshipException() {
        assertThrows(NotExistingRelationshipException.class, () -> {
            user1.updateDebt(user2, AMOUNT, Optional.of(REASON));
        }, "Update debt should throw when no such debt exist!");
    }
}
