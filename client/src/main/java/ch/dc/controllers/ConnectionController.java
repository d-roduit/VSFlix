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
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionController {

    private final static String viewName = "Connection";

    private final Router router = Router.getInstance();
    private final ClientModel clientModel = ClientModel.getInstance();
    private final ClientHttpServerModel clientHttpServerModel = ClientHttpServerModel.getInstance();

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
                String serverAddressText = serverAddressTextField.getText();
                int serverPortText = Integer.parseInt(serverPortTextField.getText());

                System.out.println(serverAddressText);

                InetAddress serverAddress = InetAddress.getByName(serverAddressText);

                System.out.println(serverAddress.getHostAddress());

                Socket clientSocket = new Socket(serverAddress, serverPortText);

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

                clientModel.setClientSocket(clientSocket);
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
}
