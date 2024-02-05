package server.data.group;

import server.data.user.User;

import java.util.List;

public record Group(String name, String groupCreator, List<User> participants) {
}
