package ch.dc.controllers;

import ch.dc.Client;
import ch.dc.Router;
import ch.dc.models.ClientModel;
import ch.dc.viewModels.FileEntry;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.MapChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;

import java.io.IOException;

public class AudioPlayerController {
    private final static String viewName = "AudioPlayer";

    private final Router router = Router.getInstance();
    private final ClientModel clientModel = ClientModel.getInstance();

    private Media media;

    private String artist;
    private String title;
    private Image albumCover;

    @FXML
    private Label artistLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private ImageView albumCoverImageView;

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
        router.setCurrentRoute(viewName);

        FileEntry fileToPlay = clientModel.getFileToPlay();
        String fileName = fileToPlay.getFile().getName();

//        String mediaSourcePath = "http://127.0.0.1/cassius.mp3";
        String mediaSourcePath = fileName.replace(" ", "%20");
        mediaSourcePath = "http://127.0.0.1/VSFlix%20Samples/Audio/" + mediaSourcePath;

        artistLabel.setVisible(false);
        artistLabel.setManaged(false);

        titleLabel.setText(fileName);

        Media media = new Media(mediaSourcePath);

        media.getMetadata().addListener((MapChangeListener<? super String, ? super Object>) (change) -> {
            if (change.wasAdded()) {
                handleMetadata(change.getKey(), change.getValueAdded());
            }
        });

        playerViewController.initializePlayer(media, mediaView);

        mediaView.setVisible(false);
        mediaView.setManaged(false);

        returnToPreviousViewButton.setOnAction(actionEvent -> displayLayoutView());
    }

    private void handleMetadata(String key, Object value) {
        switch (key) {
            case "title":
                title = value.toString();
                titleLabel.setManaged(true);
                titleLabel.setVisible(true);
                titleLabel.setText(title);
                break;
            case "artist":
                artist = value.toString();
                artistLabel.setManaged(true);
                artistLabel.setVisible(true);
                artistLabel.setText(artist);
                break;
            case "image":
                albumCover = (Image) value;
                albumCoverImageView.setImage(albumCover);
                break;
        }
    }

    private void displayLayoutView() {
        Task<Parent> loadView = new Task<Parent>() {
            @Override
            public Parent call() throws IOException {
                // If the previous route was a composed view,
                // request the same composed view when going back.
                String[] previousRoute = router.getPreviousRoute();
                for (String route: previousRoute) {
                    System.out.println(route);
                }
                if (router.isComposedRoute(previousRoute)) {
                    System.out.println("is composed");
                    router.requestNextRoutePartialView(previousRoute[1]);
                }

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
                if (playerViewController != null) {
                    if (playerViewController.mediaPlayer != null) {
                        playerViewController.mediaPlayer.stop();
                    }
                }

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
