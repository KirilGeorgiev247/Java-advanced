package server.repository;

import server.response.Response;

import java.math.BigDecimal;
import java.util.List;

public interface UserRepository {

    Response addFriend(String clientUsername, String friendUsername);

    Response announcePayOff(String clientUsername, BigDecimal amount, String friendUsername);

    Response getStatus(String username);

    Response split(String clientUsername, String username, BigDecimal amount);

    Response splitGroup(String clientUsername, String groupName, BigDecimal amount);

    Response createGroup(String clientUsername, String groupName, List<String> usernames);

    Response createUser(String username, String password, String rePassword);

    Response loginUser(String username, String password);

    Response logOutUser(String username);


//    Response getUserByUsername(String username);
//
//    Response getUserGroups(String username);
//
//    Response getUserFriends(String username);
//
//    Response deleteUser(String username);
//
//    Response getUserById(String id);
//
//    Response getUsers();
//
//    Response getGroups();
}
