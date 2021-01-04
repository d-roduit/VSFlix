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
            try {
                Socket clientSocket = connection.getValue();

                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                ObjectOutputStream objOut = new ObjectOutputStream(outputStream);
                ObjectInputStream objIn = new ObjectInputStream(inputStream);

                clientModel.setServerAddress(serverAddress);
                clientModel.setServerPort(serverPort);
                clientModel.setClientSocket(clientSocket);
                clientModel.setIp(getIPv4LocalAddress().getHostAddress());
                clientModel.setObjIn(objIn);
                clientModel.setObjOut(objOut);

                objOut.writeUTF(Command.HTTPPORT.value + " " + clientHttpServerModel.getPort());
                objOut.flush();

                String httpStatus = objIn.readUTF();
                //TODO: Log
                System.out.println("httpStatus : " + httpStatus);

                Client.setRoot("Layout");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        connection.setOnFailed(e -> {
            // TODO: Log
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
        List<InetAddress> addressList = new ArrayList<>();

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

                    addressList.add(address);
                }
            }

            Optional<InetAddress> wlanAddress = addressList
                    .stream()
                    .filter(address -> address.getHostAddress().startsWith("wlan"))
                    .findFirst();

            if (wlanAddress.isPresent()) {
                localAddress = wlanAddress.get();
            } else {
                Optional<InetAddress> ethAddress = addressList
                        .stream()
                        .filter(address -> address.getHostAddress().startsWith("eth"))
                        .findFirst();

                if (ethAddress.isPresent()) {
                    localAddress = ethAddress.get();
                } else {
                    localAddress = InetAddress.getLocalHost();
                }
            }

        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }

        return localAddress;
    }
}
