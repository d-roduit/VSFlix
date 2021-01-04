package ch.dc;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * <b>Server is the class that handles the files shared by the clients.</b>
 * The server uses threads (via the {@see ClientHandler} class) to exchange commands with multiple clients at the same time.
 */
public class Server {

    /**
     * The server logger.
     */
    public static Logger logger;

    /**
     * The port that the server listens to.
     */
    private int port = 45000;

    /**
     * The clients connected.
     */
    private List<Client> clients = new ArrayList<>();

    /**
     * The socket the server uses to listen to new connections.
     */
    private ServerSocket socketServer;

    public Server() { }

    public Server(int port) {
        this.port = port;
    }

    /**
     * Starts the server.
     */
    public void start() {

        try {
            logger.info("Server starts...");
            logger.info("Server listening on 0.0.0.0:" + port);

            socketServer = new ServerSocket(port,0);

            //wait for client connection
            System.out.println("Waiting for a client connection:");
            System.out.println("------------------------------------------");

            //infinite loop
            while (true) {
                Socket socket = null;

                try {
                    socket = socketServer.accept();

                    Client client = new Client(socket);
                    clients.add(client);

                    System.out.println("------------------------------------------");
                    System.out.println("Client (" + client.getIp() + ":" + client.getExchangingPort() + ") is connected");
                    System.out.println("------------------------------------------");

                    //the thread is created here
                    Thread clientHandler = new ClientHandler(this, client);

                    //starting the thread
                    clientHandler.start();
                } catch (Exception e) {
                    if (socket != null) {
                        socket.close();
                    }
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the list of connected clients.
     *
     * @return The list of connected clients.
     *
     * @see Client
     */
    public List<Client> getClients() {
        return clients;
    }
}
