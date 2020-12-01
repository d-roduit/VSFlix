package ch.dc;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;

public class WebViewController {

    private WebEngine webEngine;

    @FXML
    private WebView webView;

    @FXML
    private void switchToPrimary() throws IOException {
        Client.setRoot("primary");

    }

    @FXML
    public void initialize() {

        // Files possible :
        // https://droduit.ch/vsflix/lacalin.mp4
//             https://droduit.ch/vsflix/cassius.mp3
//             https://droduit.ch/vsflix/audio.wav
//        String path = "https://droduit.ch/vsflix/lacalin.mp4";
        String path = "http://127.0.0.1:8500/";
//        String path = "https://droduit.ch/vsflix/cassius.mp3";
//        String path = "http://178.194.94.142:45001/lacalin.mp4";
        //Instantiating Media class
//        Media media = new Media(new File(path).toURI().toString());

        webEngine = webView.getEngine();

        webEngine.load(path);


    }
}