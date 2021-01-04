package ch.dc.controllers;

import ch.dc.Client;
import ch.dc.FileEntry;
import ch.dc.Router;
import ch.dc.models.ClientHttpServerModel;
import ch.dc.models.ClientModel;
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
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaView;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AudioPlayerController {
    private final static String viewName = "AudioPlayer";

    private final Router router = Router.getInstance();
    private final ClientModel clientModel = ClientModel.getInstance();
    private final ClientHttpServerModel clientHttpServerModel = ClientHttpServerModel.getInstance();

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
        String filePath = URLEncoder.encode(fileToPlay.getFile().getPath(), StandardCharsets.UTF_8).replace("+", "%20");
        String clientIpAddress = fileToPlay.getClientIp();
        String clientHttpServerContextPath = clientHttpServerModel.getContextPath();
        int clientHttpServerPort = clientHttpServerModel.getPort();

        String mediaSourcePath = "http://" + clientIpAddress + ":" + clientHttpServerPort + clientHttpServerContextPath + filePath;

        artistLabel.setVisible(false);
        artistLabel.setManaged(false);

        titleLabel.setText(fileName);

        media = new Media(mediaSourcePath);

        media.setOnError(() -> {
            // TODO: Log Error

            artistLabel.setVisible(false);
            artistLabel.setManaged(false);

            handleMediaError(media.getError().getType());
        });

        media.getMetadata().addListener((MapChangeListener<? super String, ? super Object>) (change) -> {
            if (change.wasAdded()) {
                handleMetadata(change.getKey(), change.getValueAdded());
            }
        });

        playerViewController.initializePlayer(media, mediaView);

        playerViewController.mediaPlayer.setOnError(() -> {
            handleMediaError(playerViewController.mediaPlayer.getError().getType());
        });

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

    private void handleMediaError(MediaException.Type mediaExceptionType) {
        StringBuilder errorMessage = new StringBuilder("Media Error : ");

        switch (mediaExceptionType) {
            case MEDIA_CORRUPTED:
                errorMessage.append("Media corrupted");
                break;
            case MEDIA_INACCESSIBLE:
                errorMessage.append("Media inaccessible");
                break;
            case MEDIA_UNAVAILABLE:
                errorMessage.append("Media unavailable");
                break;
            case MEDIA_UNSUPPORTED:
                errorMessage.append("Media unsupported");
                break;
            case MEDIA_UNSPECIFIED:
                errorMessage.append("Media unspecified");
                break;
            case UNKNOWN:
                errorMessage.append("Media invalid");
                break;
            case OPERATION_UNSUPPORTED:
                errorMessage.append("Operation unsupported");
                break;
            case PLAYBACK_ERROR:
                errorMessage.append("Playback error");
                break;
            case PLAYBACK_HALTED:
                errorMessage.append("Playback halted");
                break;
        }

        System.err.println(errorMessage);

        // TODO: Log Error
        titleLabel.setText(errorMessage.toString());
    }

    private void displayLayoutView() {
        Task<Parent> loadView = new Task<Parent>() {
            @Override
            public Parent call() throws IOException {
                // If the previous route was a composed view,
                // request the same composed view when going back.
                String[] previousRoute = router.getPreviousRoute();

                if (router.isComposedRoute(previousRoute)) {
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
                        playerViewController.mediaPlayer.dispose();
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
