package ch.dc.controllers;

import ch.dc.Client;
import ch.dc.Command;
import ch.dc.Router;
import ch.dc.models.ClientHttpServerModel;
import ch.dc.models.ClientModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
    private Button connectButton;


    @FXML
    public void initialize() {
        router.setCurrentRoute(viewName);

        connectButton.setOnAction(actionEvent -> {
            connectToServer();
        });

        serverPortLabel.setOnMouseClicked(mouseEvent -> serverPortTextField.requestFocus());
        serverAddressLabel.setOnMouseClicked(mouseEvent -> serverAddressTextField.requestFocus());
    }

    @FXML
    private void connectToServer() {
        String serverAddressText = serverAddressTextField.getText();
        int serverPortText = Integer.parseInt(serverPortTextField.getText());

        try {
            InetAddress serverAddress = InetAddress.getByName(serverAddressText);

            Socket clientSocket = new Socket(serverAddress, serverPortText);

            BufferedReader bIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter pOut = new PrintWriter(clientSocket.getOutputStream(), true);

            clientModel.setClientSocket(clientSocket);
            clientModel.setBIn(bIn);
            clientModel.setPOut(pOut);

            pOut.println(Command.HTTPPORT.value + " " + clientHttpServerModel.getPort());

            Client.setRoot("Layout");
        } catch (IOException ioException) {
            // TODO: Log
            ioException.printStackTrace();
        }
    }

    private Label createErrorLabel(String errorMessage) {
//        connectionErrorLabel
    return new Label();
    }
}
