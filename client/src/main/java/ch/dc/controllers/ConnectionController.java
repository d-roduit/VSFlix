package ch.dc.controllers;

import ch.dc.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ConnectionController {
    @FXML
    private Button connectButton;

    @FXML   
    private TextField ipAddressTextField;
    @FXML
    private TextField portNumberTextField;


    @FXML
    public void initialize() {
        connectButton.setOnAction(actionEvent -> {
            try {
                connectToServer();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    @FXML
    private void connectToServer() throws IOException {
//        String ipAddress = ipAddressTextField.getText();
//        int portNumber = Integer.parseInt(portNumberTextField.getText());
//
//        Socket clientSocket = new Socket(InetAddress.getByName(ipAddress), portNumber);

        Client.setRoot("Layout");
    }

//    @FXML
//    private void switchToSecondary() throws IOException {
//        Client.setRoot("Secondary");
//    }

    @FXML
    private void focusIpAddressTextField() {
        ipAddressTextField.requestFocus();
    }

    @FXML
    private void focusPortNumberTextField() {
        portNumberTextField.requestFocus();
    }
}
