package ch.dc;

import ch.dc.models.ClientHttpServerModel;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ClientHttpServer {

    public static Logger logger;

    private HttpServer server = null;

    private final String contextPath = "/";

    private final ClientHttpServerModel clientHttpServerModel = ClientHttpServerModel.getInstance();

    public ClientHttpServer() {
        try {
            server = HttpServer.create();

            ClientHttpServer.logger.info("Http Server created");

            InetSocketAddress inetSocketAddress = new InetSocketAddress(0);
            server.bind(inetSocketAddress, 0);

            int httpServerPort = server.getAddress().getPort();

            clientHttpServerModel.setPort(httpServerPort);
            clientHttpServerModel.setContextPath(contextPath);

            ClientHttpServer.logger.info("Http Server ip : " + server.getAddress().getHostString());
            ClientHttpServer.logger.info("Http Server port : " + httpServerPort);
            ClientHttpServer.logger.info("Http Server context path : " + contextPath);

            server.createContext(contextPath).setHandler(this::handleRequest);
            server.setExecutor(Executors.newCachedThreadPool());
        } catch (Exception e) {
            ClientHttpServer.logger.severe("Http Server could not be created (" + e.getMessage() + ")");
        }
    }

    public void start() {
        ClientHttpServer.logger.info("Http Server starting...");

        if (server == null) {
            ClientHttpServer.logger.info("Http Server not started.");
            return;
        }

        server.start();
        ClientHttpServer.logger.info("Http Server started.");
    }

    private void handleRequest(HttpExchange exchange) {
        try {
            String requestMethod = exchange.getRequestMethod();
            Headers responseHeaders = exchange.getResponseHeaders();

            ClientHttpServer.logger.info("Request method : " + requestMethod);
            ClientHttpServer.logger.info("Local address : " + exchange.getLocalAddress().getHostString());
            ClientHttpServer.logger.info("Remote address : " + exchange.getRemoteAddress().getHostString());

            consumeRequestBodyInputStream(exchange.getRequestBody());

            String filePath = extractFileAbsolutePath(exchange.getRequestURI());

            File fileToSend = new File(filePath);

            ClientHttpServer.logger.info("File path received : " + filePath);

            if (!fileToSend.exists() || !fileToSend.isFile() || !isFileExtensionSupported(fileToSend)) {
                ClientHttpServer.logger.warning("Incorrect file path (file not exists || is not file || file extension not supported).");

                ClientHttpServer.logger.info("Sending 404 response headers...");
                exchange.sendResponseHeaders(404, 0);

                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(0);
                outputStream.flush();
                outputStream.close();

                exchange.close();

                ClientHttpServer.logger.info("Exchange closed.");
                return;
            }

            ClientHttpServer.logger.info("File OK.");

            int fileBytesSize = (int) fileToSend.length();
            byte[] fileByteArray = new byte[fileBytesSize];

            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileToSend));
            bufferedInputStream.read(fileByteArray, 0, fileBytesSize);

            String contentType = getContentType(fileToSend);

            ClientHttpServer.logger.info("Exchange response Content-Type : " + contentType);

            if (contentType != null) {
                responseHeaders.add("Content-Type", contentType);
            }

            ClientHttpServer.logger.info("Content-Length : " + String.valueOf(fileBytesSize));

            responseHeaders.add("Content-Length", String.valueOf(fileBytesSize));

            if (requestMethod.equals("HEAD")) {
                exchange.sendResponseHeaders(200, -1);
            } else {
                exchange.sendResponseHeaders(200, fileBytesSize); // Response code and length

                BufferedOutputStream bufferedOutputStream = null;
                bufferedOutputStream = new BufferedOutputStream(exchange.getResponseBody());
                bufferedOutputStream.write(fileByteArray);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();

                exchange.close();

                ClientHttpServer.logger.info("Exchange closed.");
            }
        } catch (Exception e) {
            ClientHttpServer.logger.severe("Exception occured while handling HTTP exchange (" + e.getMessage() + ").");
        }
    }

    private void printHeaders(Headers headers) {
        for (Map.Entry<String, List<String>> entries : headers.entrySet()) {
            System.out.println(entries.getKey() + "/" + entries.getValue());
        }
    }

    private void consumeRequestBodyInputStream(InputStream requestBody) {
        ClientHttpServer.logger.info("Reading the HTTP request body...");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody))) {
            while (reader.ready()) {
                reader.readLine();
            }
        } catch (Exception e) {
            ClientHttpServer.logger.severe("Exception occured while reading the HTTP request body (" + e.getMessage() + ").");
        }
    }

    private String extractFileAbsolutePath(URI requestURI) {
        String requestString = requestURI.getPath();
        return requestString.substring(contextPath.length());
    }

    private boolean isFileExtensionSupported(File file) {
        String fileExtension = extractFileExtension(file);

        boolean isMediaFile = false;

        switch (fileExtension) {
            case "mp4":
            case "mp3":
            case "wav":
            case "m4a":
                isMediaFile = true;
        }

        return isMediaFile;
    }

    private String getContentType(File file) {
        String fileExtension = extractFileExtension(file);

        String contentType = null;

        switch (fileExtension) {
            case "mp4":
                contentType = "video/mp4";
                break;
            case "mp3":
                contentType = "audio/mpeg";
                break;
            case "wav":
                contentType = "video/x-wav";
                break;
            case "m4a":
                contentType = "audio/m4a";
                break;
        }

        return contentType;
    }

    private String extractFileExtension(File file) {
        String filePath = file.toURI().toString();
        int filePathLength = filePath.length();
        return filePath.substring(filePathLength - 3, filePathLength);
    }

    public void stop() {
        ClientHttpServer.logger.info("Http Server stopping...");
        server.stop(0);
    }
}