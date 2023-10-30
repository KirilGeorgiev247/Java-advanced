package bg.sofia.uni.fmi.mjt.udemy.exception;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException() {
        super("Account not found");
    }
}
