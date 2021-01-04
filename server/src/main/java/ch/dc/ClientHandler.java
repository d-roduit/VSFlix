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
                System.out.println("Client (" + client.getIp() + ":" + client.getExchangingPort() + ") request : " + messageReceived);
                Command command = Command.valueOf(msgArray[0]);
                switch (command) {
                    case HTTPPORT:
                        int clientHttpPort = Integer.parseInt(msgArray[1]);
                        System.out.println("Http port : " + clientHttpPort);
                        client.setHttpPort(clientHttpPort);
                        objOut.writeUTF("HTTPPORT OK");
                        objOut.flush();
                        break;
                    case GETALLFILES:
                        objOut.writeObject(getAllFiles());
                        objOut.flush();
                        break;
                    case ADDFILE:
                        System.out.println("Add file.");
                        try {
                            FileEntry fileEntry = (FileEntry) objIn.readObject();
                            client.addFileEntry(fileEntry);
                            objOut.writeUTF("ADDFILE OK");
                        } catch (ClassNotFoundException e) {
                            // TODO: Log
                            objOut.writeUTF("ADDFILE KO");
                            e.printStackTrace();
                        }
                        objOut.flush();

//                        for (Client client: server.getClients()) {
//                            if (!client.equals(this.client)) {
//                                ObjectOutputStream clientObjOut = new ObjectOutputStream(client.getSocket().getOutputStream());
//                                clientObjOut.writeUTF(Command.UPDATEALLFILES.value);
//                            }
//                        }
                        break;
                    case UNSHAREFILE:
                        System.out.println("Remove a file.");
                        try {
                            FileEntry fileEntry = (FileEntry) objIn.readObject();
                            client.removeFileEntry(fileEntry);
                            objOut.writeUTF("UNSHAREFILE OK");
                        } catch (ClassNotFoundException e) {
                            // TODO: Log
                            objOut.writeUTF("UNSHAREFILE KO");
                            e.printStackTrace();
                        }
                        objOut.flush();
                        break;
                    case GETCONNECTEDCLIENTS:
                        objOut.writeUTF(getConnectedClients());
                        objOut.flush();
                        break;
                    case DISCONNECT:
                        System.out.println("Client (" + client.getIp() + ":" + client.getExchangingPort() + ") disconnect... Closing this connection.");
                        server.getClients().remove(client);
                        disconnect = true;
                        break;
                    default:
                        objOut.writeUTF("INVALID INPUT");
                        objOut.flush();
                }
            }catch (SocketException se) {
                server.getClients().remove(client);
                disconnect = true;
                se.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        try {
            client.getSocket().close();
            System.out.println("Connection closed.");
            objIn.close();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Returns the list of connected clients.
     *
     * @see Client
     */
    private String getConnectedClients() {
        StringBuilder connectedClientsString = new StringBuilder();
        connectedClientsString.append("------------------------------------------");
//        connectedClientsString.append(System.getProperty("line.separator"));
        connectedClientsString.append("Client connected to the server :");
//        connectedClientsString.append(System.getProperty("line.separator"));
        for (Client client: server.getClients()) {
            connectedClientsString.append("Client ip: " + client.getIp() + " exchanging port: " + client.getExchangingPort() +
                    " http port : " + client.getHttpPort());
//            connectedClientsString.append(System.getProperty("line.separator"));
        }
        connectedClientsString.append("------------------------------------------");

        return connectedClientsString.toString();
    }

    /**
     * Returns the list of all shared files.
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
