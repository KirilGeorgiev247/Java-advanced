package server.repository;

import server.response.Response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Response addFriend(String clientUsername, String friendUsername);

    Response announcePayOff(String clientUsername, BigDecimal amount, String friendUsername);

//    Response announceGroupPayOff(String clientUsername, BigDecimal amount, String groupName, String payer);

    Response getStatus(String username);

    Response split(String clientUsername, String username, BigDecimal amount, Optional<String> reason);

    Response splitGroup(String clientUsername, String groupName, BigDecimal amount, Optional<String> reason);

    Response createGroup(String clientUsername, String groupName, List<String> usernames);

    Response createUser(String clientUsername, String username, String password, String rePassword);

    Response loginUser(String clientUsername, String username, String password);

    Response logOutUser(String username);

    Response paymentHistory(String username);
}
