package server.storage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import config.Config;
import logger.Logger;
import server.data.group.Group;
import server.data.notification.Notification;
import server.data.user.User;
import server.exception.AlreadyExistsException;
import server.exception.NotExistingGroupExeption;
import server.exception.NotExistingUserException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class FileStorage implements Storage {

    private final Map<String, User> users = new HashMap<>();

    private final Map<String, Group> groups = new HashMap<>();

    private final Map<String, Queue<Notification>> paymentHistory = new HashMap<>();

    private final Gson gson = new Gson();
    private final String storagePath;

    public FileStorage(String storagePath) {
        this.storagePath = storagePath;
        loadData();
    }

    @Override
    public void save() {
        try {
            saveData();
        } catch (IOException e) {
            Logger.logError(e.getMessage(), e);
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    @Override
    public User getUser(String username) throws NotExistingUserException {
        if (users.containsKey(username)) {
            return users.get(username);
        }

        throw new NotExistingUserException("User with such username does not exist!");
    }

    @Override
    public Group getGroup(String name) throws NotExistingGroupExeption {
        if (groups.containsKey(name)) {
            return groups.get(name);
        }

        throw new NotExistingGroupExeption("Group with such name does not exist!");
    }

    @Override
    public void addUser(User user) throws AlreadyExistsException {
        if (users.containsKey(user.getUsername())) {
            throw new AlreadyExistsException("User with such username already exist!");
        }

        users.put(user.getUsername(), user);

        try {
            saveData();
        } catch (IOException e) {
            Logger.logError("Failed to save user", e);
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    @Override
    public void addGroup(Group group) throws AlreadyExistsException {
        if (groups.containsKey(group.name())) {
            throw new AlreadyExistsException("Group with such name already exist!");
        }

        groups.put(group.name(), group);

        try {
            saveData();
        } catch (IOException e) {
            Logger.logError("Failed to save group", e);
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    @Override
    public void addPaymentActionToHistory(User user, Notification notification) {
        if (paymentHistory.containsKey(user.getUsername())) {
            paymentHistory.get(user.getUsername()).add(notification);
        } else {
            Queue<Notification> initPaymentHistory = new ArrayDeque<>(List.of(notification));
            paymentHistory.put(user.getUsername(), initPaymentHistory);
        }

        try {
            saveHistory(user);
        } catch (IOException e) {
            Logger.logError("Failed to save payment actions history for " + user.getUsername(), e);
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    @Override
    public Queue<Notification> getUserPaymentHistory(User user) {
        loadHistory(user);
        return paymentHistory.get(user.getUsername());
    }

    @Override
    public Map<String, User> getUsers() {
        return users;
    }

    @Override
    public Map<String, Group> getGroups() {
        return groups;
    }

    private void saveData() throws IOException {
        synchronized (this) {
            try (Writer writer = new FileWriter(storagePath)) {
                Map<String, Object> allData = new HashMap<>();
                allData.put("users", users);
                allData.put("groups", groups);
                gson.toJson(allData, writer);
            }
        }
    }

    private void loadData() {
        synchronized (this) {
            if (!Files.exists(Paths.get(storagePath))) {
                return;
            }
            try (Reader reader = new FileReader(storagePath)) {
                Type usersType = new TypeToken<HashMap<String, User>>() {
                }.getType();
                Type groupsType = new TypeToken<HashMap<String, Group>>() {
                }.getType();
                Map<String, Object> allData = gson.fromJson(reader, new TypeToken<HashMap<String, Object>>() {
                }.getType());

                if (allData != null) {
                    users.clear();
                    Map<String, User> usersMap = gson.fromJson(gson.toJson(allData.get("users")), usersType);
                    users.putAll(usersMap);

                    groups.clear();
                    Map<String, Group> groupsMap = gson.fromJson(gson.toJson(allData.get("groups")), groupsType);
                    groups.putAll(groupsMap);
                }
            } catch (IOException e) {
                Logger.logError("Failed to load data", e);
                throw new UncheckedIOException(e.getMessage(), e);
            }
        }
    }

    private void saveHistory(User user) throws IOException {
        synchronized (this) {
            try (Writer writer = new FileWriter(Config.getUserHistoryFile(user))) {
                Queue<Notification> userHistory =
                    paymentHistory.getOrDefault(user.getUsername(), new ArrayDeque<>());
                gson.toJson(userHistory, writer);
            }
        }
    }

    private void loadHistory(User user) {
        synchronized (this) {
            if (!Files.exists(Paths.get(Config.getUserHistoryFile(user)))) {
                return;
            }
            try (Reader reader = new FileReader(Config.getUserHistoryFile(user))) {
                Type dataType = new TypeToken<Queue<Notification>>() {
                }.getType();
                Queue<Notification> userHistory = gson.fromJson(reader, dataType);

                if (userHistory != null) {
                    paymentHistory.put(user.getUsername(), userHistory);
                }
            } catch (IOException e) {
                Logger.logError("Failed to load payment actions history for " + user.getUsername(), e);
                throw new UncheckedIOException(e.getMessage(), e);
            }
        }
    }
}
