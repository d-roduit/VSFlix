package ch.dc;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Server {
    private List<Client> clients = new ArrayList<>();
    private InetAddress localAddress = null;
    private ServerSocket socketServer;
    private String interfaceName = "wlan0";

    public void start() {

        try {

            NetworkInterface ni = NetworkInterface.getByName(interfaceName);
            Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress ia = inetAddresses.nextElement();

                if (!ia.isLinkLocalAddress()) {
                    if (!ia.isLoopbackAddress()) {
                        System.out.println(ni.getName() + "->IP: " + ia.getHostAddress());
                        localAddress = ia;
                    }
                }
            }

            socketServer = new ServerSocket(45000,0, localAddress);

            //wait for client connection
            System.out.println("Waiting for a client connection:");
            System.out.println("------------------------------------------");

            //infinite loop
            while(true) {
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


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<Client> getClients() {
        return clients;
    }
}
