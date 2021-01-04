package ch.dc.controllers;

import ch.dc.Client;
import ch.dc.Router;
import ch.dc.models.ClientHttpServerModel;
import ch.dc.models.ClientModel;
import ch.dc.FileEntry;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaView;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class VideoPlayerController {
    private final static String viewName = "VideoPlayer";

    private final Router router = Router.getInstance();
    private final ClientModel clientModel = ClientModel.getInstance();
    private final ClientHttpServerModel clientHttpServerModel = ClientHttpServerModel.getInstance();

    private Media media;

    @FXML
    private VBox mediaContentInnerBox;

    @FXML
    private Label videoTitle;

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

        media = new Media(mediaSourcePath);

        media.setOnError(() -> {
            // TODO: Log Error
            handleMediaError(media.getError().getType());
        });

        videoTitle.setText(fileName);

        playerViewController.initializePlayer(media, mediaView);

        playerViewController.mediaPlayer.setOnError(() -> {
            handleMediaError(playerViewController.mediaPlayer.getError().getType());
        });

        mediaView.fitWidthProperty().bind(pageContentContainer.widthProperty().multiply(0.7));
        mediaView.fitHeightProperty().bind(pageContentContainer.heightProperty().multiply(0.6));

        returnToPreviousViewButton.setOnAction(actionEvent -> displayLayoutView());
    }

    private void handleMediaError(MediaException.Type mediaExceptionType) {
        mediaContentInnerBox.getChildren().remove(mediaView);

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

        Label errorLabel = new Label(errorMessage.toString());
        errorLabel.setStyle("-fx-text-fill: white");

        // TODO: Log Error
        mediaContentInnerBox.getChildren().add(errorLabel);
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
