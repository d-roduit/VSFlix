package ch.dc.controllers;

import ch.dc.Client;
import ch.dc.Router;
import ch.dc.models.ClientModel;
import ch.dc.viewModels.FileEntry;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;

import java.io.IOException;

public class VideoPlayerController {
    private final static String viewName = "VideoPlayer";

    private final Router router = Router.getInstance();
    private final ClientModel clientModel = ClientModel.getInstance();

    private Media media;

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

//        String mediaSourcePath = "http://127.0.0.1/Komo%20SF%20horizontale.mp4";
        String mediaSourcePath = fileToPlay.getFile().getName().replace(" ", "%20");
        mediaSourcePath = "http://127.0.0.1/VSFlix%20Samples/Video/" + mediaSourcePath;

        media = new Media(mediaSourcePath);

        videoTitle.setText(fileToPlay.getFile().getName());

        playerViewController.initializePlayer(media, mediaView);

        mediaView.fitWidthProperty().bind(pageContentContainer.widthProperty().multiply(0.7));
        mediaView.fitHeightProperty().bind(pageContentContainer.heightProperty().multiply(0.6));

        returnToPreviousViewButton.setOnAction(actionEvent -> displayLayoutView());
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
