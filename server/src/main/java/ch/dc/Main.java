package ch.dc;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        FileHandler fileHandler = initializeFileHandler("Server");
        Server.logger = initializeLogger("ServerLogger", fileHandler);

        int port = 50000;

        if (args.length > 0) {
            try {
                int userPort = Integer.parseInt(args[0]);

                if (userPort >= 49152 && userPort <= 65535) {
                    port = userPort;
                }
            } catch (NumberFormatException numberFormatException) {
                Server.logger.severe("Port given by user (\"" + args[0] + "\") could not be parsed as Int (" + numberFormatException.getMessage() + ")");
            }
        }

        Server server = new Server(port);
        Server.logger.info("Server starting...");
        server.start();
    }

    private static Logger initializeLogger(String loggerName, FileHandler fileHandler) {
        Logger logger = Logger.getLogger(loggerName);
        logger.addHandler(fileHandler);
        return logger;
    }

    private static FileHandler initializeFileHandler(String fileName) {
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonth().getValue();
        int year = currentDate.getYear();

        String fileNamePath = fileName + "-" + month + "-" + year + ".log";
        File logFile = new File(fileNamePath);

        boolean append = false;

        if (logFile.exists() && logFile.isFile()) {
            append = true;
        }

        FileHandler fileHandler = null;

        try {
            fileHandler = new FileHandler(fileNamePath, append);
            fileHandler.setFormatter(new LoggingFormatter());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        return fileHandler;
    }


}
