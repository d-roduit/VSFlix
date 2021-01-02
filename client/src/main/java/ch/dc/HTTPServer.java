package ch.dc;

import ch.dc.models.ClientModel;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class HTTPServer {
    private final ClientModel clientModel = ClientModel.getInstance();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);

//        clientModel.setHTTPServerPort(port);

        HttpContext context = server.createContext("/");
        context.setHandler(HTTPServer::handleRequest);
        server.start();
    }

    private static void handleRequest(HttpExchange exchange) {
        try {
            Headers headers = exchange.getResponseHeaders();

            // Send the song
            File fileToSend = new File("C:\\wamp64\\www\\lacalin.mp4");
//            File fileToSend = new File("C:\\wamp64\\www\\audio.mp3");

            int fileBytesSize = (int) fileToSend.length();
            byte[] myByteArray = new byte[fileBytesSize];

            FileInputStream fis = null;
            fis = new FileInputStream(fileToSend);

            BufferedInputStream bis = new BufferedInputStream(fis);

            bis.read(myByteArray, 0, myByteArray.length);
            System.out.println("myByteArray.length : " + myByteArray.length);

            System.out.println("Sending file...");

            System.out.println(exchange.getRequestMethod() + " Request");

//            headers.add("Transfer-Encoding", "chunked");
//            headers.add("Content-Disposition", "inline; filename=\"audio.mp3\"");
            headers.add("Content-Disposition", "inline; filename=\"lacalin.mp4\"");
            headers.add("Accept-Ranges", "bytes");
            headers.add("Keep-Alive", "timeout=5, max=100");
            headers.add("Connection", "keep-alive");
//            headers.add("Content-Type", "audio/mpeg");
            headers.add("Content-Type", "video/mp4");
            headers.add("Content-Length", String.valueOf(myByteArray.length));


            if (exchange.getRequestMethod().equals("HEAD")) {
                exchange.sendResponseHeaders(200, -1); // Response code and length

                for (Map.Entry<String, List<String>> entries : headers.entrySet()) {
                    System.out.println(entries.getKey() + "/" + entries.getValue());
                }

            } else {
                exchange.sendResponseHeaders(200, 0); // Response code and length

                for (Map.Entry<String, List<String>> entries : headers.entrySet()) {
                    System.out.println(entries.getKey() + "/" + entries.getValue());
                }

                OutputStream os = exchange.getResponseBody();
                os.write(myByteArray);
                os.flush();
                os.close();
                exchange.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}