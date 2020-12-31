package ch.dc.controllers;

import ch.dc.Client;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;

import java.io.IOException;

public class AudioPlayerController {
    @FXML
    public MediaView mediaView;

    @FXML
    private VBox playerView;

    @FXML
    private PlayerController playerViewController;

    @FXML
    private BorderPane pageContentContainer;

    @FXML
    private Button returnToPreviousViewButton;

    @FXML
    public void initialize() {
        String mediaSourcePath = "http://127.0.0.1/cassius.mp3";

        playerViewController.initializePlayer(mediaSourcePath, mediaView);

        mediaView.setVisible(false);
        mediaView.setManaged(false);

        returnToPreviousViewButton.setOnAction(actionEvent -> displayLayoutView());
    }

    private void displayLayoutView() {
        Task<Parent> loadView = new Task<Parent>() {
            @Override
            public Parent call() throws IOException {
                Parent fxmlContent = Client.loadFXML("Layout");

                if (isCancelled()) {
                    updateMessage("Cancelled");
                    fxmlContent = null;
                }

                return fxmlContent;
            }
        };

        loadView.setOnSucceeded(e -> {
            Parent fxmlContent = loadView.getValue();

            if (fxmlContent != null) {
                Client.scene.setRoot(fxmlContent);
            }
        });

        loadView.setOnFailed(e -> {
            // TODO: Log error with logger
            loadView.getException().printStackTrace();
        });

        loadView.setOnCancelled(e -> {
            // TODO: Log error with logger
        });

        Thread thread = new Thread(loadView);
        thread.setDaemon(true);
        thread.start();
    }
}
