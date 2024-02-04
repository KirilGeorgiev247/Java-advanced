package server.repository;

import server.response.Response;

import java.util.List;

public class MJTUserRepository implements UserRepository {

    // collection
    public MJTUserRepository() {
        // load from file by creation and initial the right collection
    }
    @Override
    public Response addFriend(String username) {
        return null;
    }

    @Override
    public Response getUserByUsername(String username) {
        return null;
    }

    @Override
    public Response getUserGroups(String username) {
        return null;
    }

    @Override
    public Response getUserFriends(String username) {
        return null;
    }

    @Override
    public Response announcePayOff(String username) {
        return null;
    }

    @Override
    public Response createGroup(String groupName, List<String> usernames) {
        return null;
    }

    @Override
    public Response createUser(String username, String password, String rePassword) {
        return null;
    }

    @Override
    public Response loginUser(String username, String password) {
        return null;
    }

    @Override
    public Response deleteUser(String username) {
        return null;
    }
}
