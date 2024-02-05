package server.data.debt;

import java.math.BigDecimal;

public class Debt {
    private String owning;
    private String reason = "";
    private BigDecimal currAmount;

    public Debt(String owning, BigDecimal amount) {
        this.owning = owning;
        currAmount = amount;
    }

    public void update(BigDecimal amount) {
        currAmount = currAmount.subtract(amount);
    }

    public BigDecimal getAmount() {
        return currAmount;
    }

    public String getOwning() {
        return owning;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
}
