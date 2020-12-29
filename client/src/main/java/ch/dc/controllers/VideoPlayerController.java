package ch.dc.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaView;

public class VideoPlayerController {

    @FXML
    private BorderPane pageContentContainer;

    @FXML
    private MediaView mediaView;

    @FXML
    public void initialize() {
        mediaView.fitWidthProperty().bind(pageContentContainer.widthProperty().multiply(0.7));
        mediaView.fitHeightProperty().bind(pageContentContainer.heightProperty().multiply(0.6));
    }
}
