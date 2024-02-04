package server.services;

import server.user.User;

import java.math.BigDecimal;

public interface DebtService {

    void payOffDebt(User owning, User beingOwned, BigDecimal amount);
}
