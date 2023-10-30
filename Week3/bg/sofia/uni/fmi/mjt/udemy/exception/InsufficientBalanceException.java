package bg.sofia.uni.fmi.mjt.udemy.exception;

public class InsufficientBalanceException extends Exception{
    public InsufficientBalanceException() {
        super("Insufficient balance");
    }
}
