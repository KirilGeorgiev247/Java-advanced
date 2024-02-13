package config;

import server.data.user.User;

public class Config {
    private static final String HISTORY_FILE_BASE = "src/server/";
    public static final String HOST = "localhost";
    public static final int PORT = 4444;
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final String STORAGE_PATH = "src/server/data.json";
    public static final String LOG_FILE = "src/application.txt";

    public static String getUserHistoryFile(User user) {
        return HISTORY_FILE_BASE + user.getUsername() + ".json";
    }
}
