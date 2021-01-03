package main.java.ch.dc;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>ClientHandler is the class that handles the thread of a client connection.</b>
 */
public class ClientHandler extends Thread {

    /**
     * The server.
     */
    private final Server server;

    /**
     * The client.
     */
    private final Client client;

    /**
     * The BufferedReader.
     */
    private final BufferedReader bIn;

    /**
     * The PrintWriter.
     */
    private final PrintWriter pOut;

    /**
     * The command enumeration.
     */
    private enum Command {
        HTTPPORT,
        GETALLFILES,
        ADDFILE,
        UNSHAREFILE,
        GETCONNECTEDCLIENTS,
        DISCONNECT
    }

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
        this.bIn = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
        this.pOut = new PrintWriter(client.getSocket().getOutputStream());
    }

    //overwrite the thread run()
    @Override
    public void run() {
        String messageReceived;
        boolean disconnect = false;
        while (!disconnect) {
            try {
                pOut.println("What is your request ? Type DISCONNECT to terminate connection.");
                pOut.flush();

                messageReceived = bIn.readLine();

                String[] msgArray = messageReceived.split(" ");
                System.out.println("Client (" + client.getIp() + ":" + client.getExchangingPort() + ") request : " + messageReceived);
                Command command = Command.valueOf(msgArray[0]);
                switch (command) {
                    case HTTPPORT:
                        int clientHttpPort = Integer.parseInt(msgArray[1]);
                        System.out.println("Http port : " + clientHttpPort);
                        client.setHttpPort(clientHttpPort);
                        pOut.println("Http port set.");
                        pOut.flush();
                        break;
                    case GETALLFILES:
                        pOut.println(getAllFiles());
                        pOut.flush();
                        break;
                    case ADDFILE:
                        System.out.println("Add a file");
                        File file = new File(msgArray[1]);
                        try{
                            FileType fileType = FileType.valueOf(msgArray[2]);
                            client.addFile(file, fileType);
                            pOut.println("File was added.");
                        }catch (Exception e){
                            pOut.println("Invalid input.");
                        }
                        pOut.flush();
                        break;
                    case UNSHAREFILE:
                        System.out.println("Remove a file");
                        File fileToUnshare = new File(msgArray[1]);
                        try{
                            FileType filetype = FileType.valueOf(msgArray[2]);
                            client.unshareFile(fileToUnshare, filetype);
                            pOut.println("File was removed.");
                        }catch (Exception e){
                            pOut.println("Invalid input");
                        }
                        pOut.flush();
                        break;
                    case GETCONNECTEDCLIENTS:
                        pOut.println(getConnectedClients());
                        pOut.flush();
                        break;
                    case DISCONNECT:
                        System.out.println("Client (" + client.getIp() + ":" + client.getExchangingPort() + ") disconnect... Closing this connection.");
                        server.getClients().remove(client);
                        disconnect = true;
                        break;
                    default:
                        pOut.println("Invalid input");
                        pOut.flush();
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
            System.out.println("Connection closed");
            bIn.close();
            pOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

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

    private String getAllFiles() {
        List<FileEntry> allFiles = new ArrayList<>();
        StringBuilder allFilesString = new StringBuilder();
        allFilesString.append("------------------------------------------");
//        allFilesString.append(System.getProperty("line.separator"));
        allFilesString.append("All files shared :");
//        allFilesString.append(System.getProperty("line.separator"));
        for (Client client: server.getClients()) {
            if (!client.equals(this.client)) {
                allFiles.addAll(client.getFiles());
            }
        }
        for(FileEntry fileEntry: allFiles) {
            allFilesString.append("- " + fileEntry.getFile().getName() + " (type : " + fileEntry.getFileType() + ")");
//                allFilesString.append(System.getProperty("line.separator"));
        }
        allFilesString.append("------------------------------------------");

        return allFilesString.toString();
    }

}
