package ch.dc.controllers;

import ch.dc.*;
import ch.dc.models.ClientHttpServerModel;
import ch.dc.models.ClientModel;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

public class ConnectionController {

    private final static String viewName = "Connection";

    private final Router router = Router.getInstance();
    private final ClientModel clientModel = ClientModel.getInstance();
    private final ClientHttpServerModel clientHttpServerModel = ClientHttpServerModel.getInstance();

    private String serverAddress;
    private int serverPort;

    @FXML
    private Label serverAddressLabel;

    @FXML
    private Label serverPortLabel;

    @FXML
    private TextField serverAddressTextField;

    @FXML
    private TextField serverPortTextField;

    @FXML
    private HBox connectionErrorBox;

    @FXML
    private Button connectButton;


    @FXML
    public void initialize() {
        router.setCurrentRoute(viewName);

        connectionErrorBox.setVisible(false);
        connectionErrorBox.setManaged(false);

        connectButton.setOnAction(actionEvent -> {
            Client.logger.info("Connect button clicked.");
            connectToServer();
        });

        serverPortLabel.setOnMouseClicked(mouseEvent -> serverPortTextField.requestFocus());
        serverAddressLabel.setOnMouseClicked(mouseEvent -> serverAddressTextField.requestFocus());
    }

    private void connectToServer() {
        Task<Socket> connection = new Task<Socket>() {
            @Override
            public Socket call() throws IOException {
                serverAddress = serverAddressTextField.getText();
                serverPort = Integer.parseInt(serverPortTextField.getText());

                InetAddress serverAddress = InetAddress.getByName(ConnectionController.this.serverAddress);

                Socket clientSocket = new Socket(serverAddress, serverPort);

                return clientSocket;
            }
        };

        connection.setOnSucceeded(e -> {
            Client.logger.info("Connection to server succeeded.");

            try {
                Socket clientSocket = connection.getValue();

                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                ObjectOutputStream objOut = new ObjectOutputStream(outputStream);
                ObjectInputStream objIn = new ObjectInputStream(inputStream);


                Client.logger.info("Server IP : " + serverAddress);
                Client.logger.info("Server port : " + serverPort);

                clientModel.setServerAddress(serverAddress);
                clientModel.setServerPort(serverPort);
                clientModel.setClientSocket(clientSocket);

                String clientIp = getIPv4LocalAddress().getHostAddress();
                Client.logger.info("Client IP : " + clientIp);

                clientModel.setIp(clientIp);
                clientModel.setObjIn(objIn);
                clientModel.setObjOut(objOut);

                Client.logger.info("Sending " + Command.HTTPPORT.value + " command...");
                objOut.writeUTF(Command.HTTPPORT.value + " " + clientHttpServerModel.getPort());
                objOut.flush();

                String httpStatus = objIn.readUTF();
                Client.logger.info("Http status received : " + httpStatus);

                Client.setRoot("Layout");
            } catch (IOException ioException) {
                Client.logger.severe("Load Layout view exception (" + ioException.getMessage() + ").");
            }
        });

        connection.setOnFailed(e -> {
            Client.logger.severe("Connection to server failed (" + connection.getException().getMessage()  + ").");
            connectionErrorBox.setVisible(true);
            connectionErrorBox.setManaged(true);
            Label errorLabel = createErrorLabel("Connection refused : Verify server address");
            connectionErrorBox.getChildren().clear();
            connectionErrorBox.getChildren().add(errorLabel);
        });

        Thread thread = new Thread(connection);
        thread.setDaemon(true);
        thread.start();
    }

    private Label createErrorLabel(String errorMessage) {
        Label errorLabel = new Label(errorMessage);
        errorLabel.setId("connectionError");
        errorLabel.setGraphic(new FontAwesomeIcon(FontAwesome.EXCLAMATION_TRIANGLE));

        return errorLabel;
    }

    /**
     * Returns the local IPv4 address of the server or 127.0.0.1 if no address was found.
     *
     * @return The local IPv4 address of the server or 127.0.0.1 if no address was found.
     *
     * @see InetAddress
     */
    private InetAddress getIPv4LocalAddress() {
        InetAddress localAddress = null;

        try {
            localAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            Client.logger.severe("");
        }

        List<InetAddress> wlanAddressList = new ArrayList<>();
        List<InetAddress> ethAddressList = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // Filters out 127.0.0.1 and inactive interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> interfaceAddresses = networkInterface.getInetAddresses();
                while (interfaceAddresses.hasMoreElements()) {
                    InetAddress address = interfaceAddresses.nextElement();

                    if (address instanceof Inet6Address) {
                        continue;
                    }

                    String interfaceName = networkInterface.getName();

                    if (interfaceName.startsWith("wlan")) {
                        wlanAddressList.add(address);
                    } else if (interfaceName.startsWith("eth")) {
                        ethAddressList.add(address);
                    }
                }
            }

            if (!wlanAddressList.isEmpty()) {
                localAddress = wlanAddressList.get(0);
            } else {
                if (!ethAddressList.isEmpty()) {
                    localAddress = ethAddressList.get(0);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        return localAddress;
    }
}
