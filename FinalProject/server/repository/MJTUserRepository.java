package server.repository;

import server.response.Response;
import server.storage.Storage;

import java.math.BigDecimal;
import java.util.List;

public class MJTUserRepository implements UserRepository {

    private final Storage storage;
    public MJTUserRepository(Storage storage) {
        this.storage = storage;
    }
    @Override
    public Response addFriend(String clientUsername, String friendUsername) {

        return null;
    }

    @Override
    public Response announcePayOff(String clientUsername, BigDecimal amount, String friendUsername) {
        return null;
    }

    @Override
    public Response getStatus(String username) {
        return null;
    }

    @Override
    public Response split(String clientUsername, String friendUsername, BigDecimal amount) {
        return null;
    }

    @Override
    public Response splitGroup(String clientUsername, String groupName, BigDecimal amount) {
        return null;
    }

    @Override
    public Response createGroup(String clientUsername, String groupName, List<String> usernames) {
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
    public Response logOutUser(String username) {
        return null;
    }
}
