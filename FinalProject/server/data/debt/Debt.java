package server.data.debt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Debt {
    private final String owning;

    private String groupName;
    public DebtType type;
    private final List<String> reasons;
    private BigDecimal currAmount;

    public Debt(String owning, BigDecimal amount, DebtType type) {
        this.owning = owning;
        currAmount = amount;
        this.type = type;
        reasons = new ArrayList<>();
    }

    public Debt(String owning, BigDecimal amount, DebtType type, String groupName) {
        this(owning, amount, type);
        this.groupName = groupName;
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

    public List<String> getReasons() {
        return reasons;
    }

    public DebtType getType() {
        return type;
    }

    public void addReason(String reason) {
        reasons.add(reason);
    }

    public String getGroupName() {
        return groupName;
    }
}
