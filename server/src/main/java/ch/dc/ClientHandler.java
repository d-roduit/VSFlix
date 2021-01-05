package ch.dc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>ClientHandler is the class that handles a client connection.</b>
 * This class is instantiated by the server each time it receives a new connection.
 */
public class ClientHandler extends Thread {

    /**
     * The server.
     */
    private final Server server;

    /**
     * The client the ClientHandler handles.
     */
    private final Client client;

    /**
     * The ObjectInputStream used to communicate with the client.
     */
    private final ObjectInputStream objIn;

    /**
     * The ObjectOutputStream used to communicate with the client.
     */
    private final ObjectOutputStream objOut;

    /**
     * ClientHandler constructor.
     *
     * @param server
     *              The server.
     * @param client
     *              The client.
     *
     * @throws IOException
     */
    public ClientHandler(Server server, Client client) throws IOException {
        this.server = server;
        this.client = client;
        this.objOut = new ObjectOutputStream(client.getSocket().getOutputStream());
        this.objIn = new ObjectInputStream(client.getSocket().getInputStream());
    }

    /**
     * Overwrite the run() method defined in the {@see Thread} class.
     */
    @Override
    public void run() {
        String messageReceived;
        boolean disconnect = false;
        while (!disconnect) {
            try {
                messageReceived = objIn.readUTF();

                String[] msgArray = messageReceived.split(" ");
                Command command = Command.valueOf(msgArray[0]);
                Server.logger.info("Command " + command.value + " received.");
                switch (command) {
                    case HTTPPORT:
                        Server.logger.info("Command parameter : " + msgArray[1]);
                        int clientHttpPort = Integer.parseInt(msgArray[1]);
                        client.setHttpPort(clientHttpPort);
                        objOut.writeUTF("HTTPPORT OK");
                        objOut.flush();
                        break;
                    case GETALLFILES:
                        objOut.writeObject(getAllFiles());
                        objOut.flush();
                        break;
                    case ADDFILE:
                        try {
                            FileEntry fileEntry = (FileEntry) objIn.readObject();
                            Server.logger.info("Command parameter : " + fileEntry.getFile().getPath());
                            client.addFileEntry(fileEntry);
                            objOut.writeUTF("ADDFILE OK");
                        } catch (ClassNotFoundException e) {
                            Server.logger.severe("Class not found exception when reading ObjectInputStream (" + e.getMessage() + ").");
                            objOut.writeUTF("ADDFILE KO");
                        }
                        objOut.flush();
                        break;
                    case UNSHAREFILE:
                        try {
                            FileEntry fileEntry = (FileEntry) objIn.readObject();
                            Server.logger.info("Command parameter : " + fileEntry.getFile().getPath());
                            client.removeFileEntry(fileEntry);
                            objOut.writeUTF("UNSHAREFILE OK");
                        } catch (ClassNotFoundException e) {
                            Server.logger.severe("Class not found exception when reading ObjectInputStream (" + e.getMessage() + ").");
                            objOut.writeUTF("UNSHAREFILE KO");
                        }
                        objOut.flush();
                        break;
                    case GETNBCONNECTEDCLIENTS:
                        String nbConnectedClients = String.valueOf(getNbConnectedClients());
                        objOut.writeUTF(nbConnectedClients);
                        objOut.flush();
                        break;
                    case DISCONNECT:
                        Server.logger.info("Client disconnecting...");
                        server.getClients().remove(client);
                        disconnect = true;
                        break;
                    default:
                        Server.logger.info("Invalid input");
                        objOut.writeUTF("INVALID INPUT");
                        objOut.flush();
                }
            } catch (SocketException se) {
                server.getClients().remove(client);
                disconnect = true;
                se.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Server.logger.info("Connection closing...");
            client.getSocket().close();
            Server.logger.info("Connection closed.");
            Server.logger.info("ObjectInputStream closing...");
            objIn.close();
            Server.logger.info("ObjectInputStream closed.");
            Server.logger.info("ObjectOutputStream closing...");
            objOut.close();
            Server.logger.info("ObjectOutputStream closed...");
        } catch (IOException e) {
            Server.logger.severe("IOException occured (" + e.getMessage() + ")");
        }
    }

    /**
     * Returns the number of connected clients.
     *
     * @return The number of connected clients.
     *
     * @see Server#getClients()
     */
    private int getNbConnectedClients() {
        return server.getClients().size();
    }

    /**
     * Returns the list of all shared files.
     *
     * @return The list of all shared files.
     *
     * @see FileEntry
     */
    private List<FileEntry> getAllFiles() {
        List<FileEntry> allFiles = new ArrayList<>();

        for (Client client: server.getClients()) {
            if (!client.equals(this.client)) {
                allFiles.addAll(client.getFiles());
            }
        }

        return allFiles;
    }

}
