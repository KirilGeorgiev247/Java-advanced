package server.repository;

import server.response.Response;

import java.util.List;

public interface UserRepository {

    Response addFriend(String username);

    Response getUserByUsername(String username);

    Response getUserGroups(String username);

    Response getUserFriends(String username);

    Response announcePayOff(String username);

    Response createGroup(String groupName, List<String> usernames);

    Response createUser(String username, String password, String rePassword);

    Response loginUser(String username, String password);

    Response deleteUser(String username);

//    Response getUserById(String id);
//
//    Response getUsers();
//
//    Response getGroups();
}
