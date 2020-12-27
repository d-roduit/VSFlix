package ch.dc;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionController {

    @FXML   
    private TextField ipAddressTextField;
    @FXML
    private TextField portNumberTextField;

    @FXML
    private void connectToServer() throws IOException {
        String ipAddress = ipAddressTextField.getText();
        int portNumber = Integer.parseInt(portNumberTextField.getText());

        Socket clientSocket = new Socket(InetAddress.getByName(ipAddress), portNumber);
    }

    @FXML
    private void switchToSecondary() throws IOException {
        Client.setRoot("secondary");
    }

    @FXML
    private void focusIpAddressTextField() {
        ipAddressTextField.requestFocus();
    }

    @FXML
    private void focusPortNumberTextField() {
        portNumberTextField.requestFocus();
    }
}
