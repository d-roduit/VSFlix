package ch.dc;

import java.awt.*;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SecondaryController {

    public Button secondaryButton;

    @FXML
    private void switchToPrimary() throws IOException {
        secondaryButton.setText("heyy changement de texte");
//        Client.setRoot("primary");

    }
}