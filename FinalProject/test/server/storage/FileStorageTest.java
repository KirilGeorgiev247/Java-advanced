package server.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import server.data.group.Group;
import server.data.user.User;
import server.exception.AlreadyExistsException;
import server.exception.NotExistingGroupExeption;
import server.exception.NotExistingUserException;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileStorageTest {

    private static final String TEST_GROUP = "bandata";
    private static final String TEST_FILE = "data.json";
    private static final String CLIENT_USERNAME = "user";
    @TempDir
    Path tempDir;
    private FileStorage fileStorage;

    @BeforeEach
    void setUp() {
        fileStorage = new FileStorage(tempDir.resolve(TEST_FILE).toString());
    }

    @Test
    void testSaveAndLoadUser() throws AlreadyExistsException, NotExistingUserException {
        User user = new User(CLIENT_USERNAME, "hashedPassword");
        fileStorage.addUser(user);

        FileStorage reloadedStorage = new FileStorage(tempDir.resolve(TEST_FILE).toString());

        User retrievedUser = reloadedStorage.getUser(CLIENT_USERNAME);
        assertEquals(user.getUsername(), retrievedUser.getUsername(),
            "The retrieved username should match the saved one!");

        Map<String, User> users = reloadedStorage.getUsers();
        assertTrue(users.containsKey(CLIENT_USERNAME), "The users map should contain the saved user!");
        assertEquals(user.getUsername(), users.get(CLIENT_USERNAME).getUsername(),
            "The username in the users map should match the saved one!");
    }

    @Test
    void testSaveAndLoadGroup() throws AlreadyExistsException, NotExistingGroupExeption {
        User user = new User(CLIENT_USERNAME, "hashedPassword");
        Group group = new Group(TEST_GROUP, "testUser", List.of(user));

        fileStorage.addGroup(group);

        FileStorage reloadedStorage = new FileStorage(tempDir.resolve(TEST_FILE).toString());

        Group retrievedGroup = reloadedStorage.getGroup(TEST_GROUP);
        assertNotNull(retrievedGroup, "The group should exist after being added!");
        assertEquals(TEST_GROUP, retrievedGroup.name(), "The retrieved group name should match the saved one!");

        Map<String, Group> groups = reloadedStorage.getGroups();
        assertTrue(groups.containsKey(TEST_GROUP), "The groups map should contain the saved group!");
        assertEquals(group.name(), groups.get(TEST_GROUP).name(),
            "The group name in the groups map should match the saved one!");
    }

    @Test
    void testSaveThrowsWhenGroupAlreadyExists() {
        User user = new User(CLIENT_USERNAME, "hashedPassword");
        Group group = new Group(TEST_GROUP, CLIENT_USERNAME, List.of(user));
        assertDoesNotThrow(() -> fileStorage.addGroup(group));

        assertThrows(AlreadyExistsException.class, () -> fileStorage.addGroup(group),
            "Adding a group with the same name should throw!");
    }

    @Test
    void testSaveThrowsWhenUserAlreadyExists() {
        User user = new User(CLIENT_USERNAME, "hashedPassword");
        assertDoesNotThrow(() -> fileStorage.addUser(user));

        assertThrows(AlreadyExistsException.class, () -> fileStorage.addUser(user),
            "Adding a user with the same username should throw!");
    }

    @Test
    void testIfFileIsCreatedByFirstSave() {
        fileStorage.save();

        File savedFile = tempDir.resolve(TEST_FILE).toFile();
        assertTrue(savedFile.exists(), "The save method should create a data file!");
    }

    @Test
    void testGetUserThrowsWhenUserNotExisting() {
        assertThrows(NotExistingUserException.class, () -> fileStorage.getUser(CLIENT_USERNAME),
            "Attempting to retrieve a non-existent user should throw!");
    }

    @Test
    void testGetGroupThrowsWhenGroupNotExisting() {
        assertThrows(NotExistingGroupExeption.class, () -> fileStorage.getGroup(TEST_GROUP),
            "Attempting to retrieve a non-existent group should throw!");
    }


}
