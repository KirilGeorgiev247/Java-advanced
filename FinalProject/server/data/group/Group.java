package server.data.group;

import server.data.user.User;

import java.util.List;

public record Group(String name, List<User> participants) {
}
