package ch.dc;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {

        FileHandler fileHandler = initializeFileHandler("Client");

        Logger clientLogger = initializeLogger("ClientLogger", fileHandler);
        Logger httpServerLogger = initializeLogger("HttpServerLogger", fileHandler);

        ClientHttpServer clientHttpServer = ClientHttpServer.getInstance();
        ClientHttpServer.logger = httpServerLogger;

        Client.logger = clientLogger;
        Client.clientHttpServer = clientHttpServer;

        Client.main(args);
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
