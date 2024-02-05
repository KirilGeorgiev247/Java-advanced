package server.startup;

import server.command.CommandExecutor;
import server.multithreaded.SplitWiseServer;
import server.repository.MJTUserRepository;
import server.repository.UserRepository;
import server.storage.FileStorage;
import server.storage.Storage;

public class StartUp {
    private static UserRepository userRepository;
    private static CommandExecutor commandExecutor;
    private static boolean isStarted = false;
    private static int currPort;
    private static Storage storage;
    private static SplitWiseServer server;

    public static void start(int port) {
        if(!isStarted) {
            currPort = port;
            storage = new FileStorage();
            userRepository = new MJTUserRepository(storage);
            commandExecutor = new CommandExecutor(userRepository);
            server = new SplitWiseServer(port, commandExecutor);
            server.start();
        }
    }

    public static void reset() {
        close();
        start(currPort);
    }

    public static void close() {
        storage.saveData();
        server.stop();
    }
}
