package server.startup;

import config.Config;
import server.algorithm.HashCalculator;
import server.algorithm.MD5HashCalculator;
import server.command.CommandExecutor;
import server.multithreaded.SplitWiseServer;
import server.repository.MJTUserRepository;
import server.repository.UserRepository;
import server.services.NotificationService;
import server.storage.FileStorage;
import server.storage.Storage;

public class StartUp {
    private static boolean isStarted = false;
    private static int currPort;
    private static Storage storage;
    private static SplitWiseServer server;
    private static NotificationService notificationService = new NotificationService();

    public static void start(int port) {
        if (!isStarted) {
            isStarted = true;
            currPort = port;
            storage = new FileStorage(Config.STORAGE_PATH);

            HashCalculator hashCalculator = new MD5HashCalculator();
            UserRepository userRepository = new MJTUserRepository(storage, hashCalculator, notificationService);
            CommandExecutor commandExecutor = new CommandExecutor(userRepository);
            server = new SplitWiseServer(port, commandExecutor);
            server.start();
        }
    }

    public static void reset() {
        close();
        start(currPort);
    }

    public static void close() {
        if (isStarted) {
            server.stop();
            isStarted = false;
        }
    }
}
