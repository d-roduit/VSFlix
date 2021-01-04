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

    private static ClientHttpServer INSTANCE;

    static {
        try {
            INSTANCE = new ClientHttpServer();
        } catch (IOException ioException) {
            // TODO: Log error
            ioException.printStackTrace();
        }
    }

    public static Logger logger;

    private final HttpServer server;

    private final String contextPath = "/";

    private final ClientHttpServerModel clientHttpServerModel = ClientHttpServerModel.getInstance();

    private ClientHttpServer() throws IOException {
        server = HttpServer.create();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(0);
        server.bind(inetSocketAddress, 0);

        int httpServerPort = server.getAddress().getPort();

        clientHttpServerModel.setPort(httpServerPort);
        clientHttpServerModel.setContextPath(contextPath);

        System.out.println("--- SERVER ADDRESS INFORMATIONS ---");
        System.out.println("Server port : " + httpServerPort);

        server.createContext(contextPath).setHandler(this::handleRequest);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    private void handleRequest(HttpExchange exchange) {
        try {
            String requestMethod = exchange.getRequestMethod();
            Headers responseHeaders = exchange.getResponseHeaders();

            System.out.println("-------- Request Information --------");
            System.out.println("Request : " + requestMethod);
            System.out.println("Local address : " + exchange.getLocalAddress().getHostString());
            System.out.println("Remote address : " + exchange.getRemoteAddress().getHostString());
            Headers requestHeaders = exchange.getRequestHeaders();
            printHeaders(requestHeaders);
            consumeRequestBodyInputStream(exchange.getRequestBody());
            System.out.println("-------- End Request Information --------\n");

            String filePath = extractFileAbsolutePath(exchange.getRequestURI());

            File fileToSend = new File(filePath);

            System.out.println("filePath received : " + filePath);

            if (!fileToSend.exists() || !fileToSend.isFile() || !isFileExtensionSupported(fileToSend)) {

//                System.out.println("|----- FICHIER PAS CORRECT -----|");

                System.out.println(fileToSend.exists());
                System.out.println(fileToSend.isFile());
                System.out.println(isFileExtensionSupported(fileToSend));

                exchange.sendResponseHeaders(404, 0);

                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(0);
                outputStream.flush();
                outputStream.close();

                exchange.close();

                return;
            }

//            System.out.println("|----- FILE VERIFIED SUCCESSFULLY -----|");

            int fileBytesSize = (int) fileToSend.length();
            byte[] fileByteArray = new byte[fileBytesSize];

            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileToSend));
            bufferedInputStream.read(fileByteArray, 0, fileBytesSize);

            String contentType = getContentType(fileToSend);
            if (contentType != null) {
                responseHeaders.add("Content-Type", contentType);
            }

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
            }

        } catch (Exception e) {
            // TODO: Log
            System.err.println("Error occurred : ");
            e.printStackTrace();
        }
    }

    private void printHeaders(Headers headers) {
        for (Map.Entry<String, List<String>> entries : headers.entrySet()) {
            System.out.println(entries.getKey() + "/" + entries.getValue());
        }
    }

    private void consumeRequestBodyInputStream(InputStream requestBody) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody))) {
            while (reader.ready()) {
                reader.readLine();
            }
        } catch (Exception e) {
            // TODO: Log error
            System.err.println("Error in requestBody reader");
            e.printStackTrace();
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
        //TODO: Log HttpServer stopping...
        server.stop(0);
    }

    public static ClientHttpServer getInstance() { return INSTANCE; }
}