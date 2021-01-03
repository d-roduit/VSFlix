import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class ClientHandler extends Thread {

    private final Server server;
    private final Client client;
    private final BufferedReader bIn;
    private final PrintWriter pOut;

    private enum Command {
        HTTPPORT,
        GETALLFILES,
        ADDFILE,
        GETCONNECTEDCLIENTS,
        DISCONNECT
    }

    //Constructor
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
