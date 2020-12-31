package ch.dc.controllers;

import ch.dc.Client;
import com.cathive.fonts.fontawesome.FontAwesomeIcon;
import com.cathive.fonts.fontawesome.FontAwesomeIconView;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Locale;

public class VideoPlayerController {

    private MediaPlayer mediaPlayer;
    private Media video;
    private boolean isVideoAtEnd = false;
    private Status mediaPlayerStatusBeforeProgressBarPressed;
    private double volumeBeforeMute = 1.0;

    @FXML
    private BorderPane pageContentContainer;

    @FXML
    private Button returnToPreviousViewButton;

    @FXML
    private MediaView mediaView;

    @FXML
    private Slider mediaProgressBar;

    @FXML
    private Label currentTimerLabel;

    @FXML
    private Label totalDurationLabel;

    @FXML
    private Button volumeButton;

    @FXML
    private FontAwesomeIconView volumeIcon;

    @FXML
    private Slider volumeBar;

    @FXML
    private Button backwardButton;

    @FXML
    private Button playButton;

    @FXML
    private FontAwesomeIconView playOrPauseIcon;

    @FXML
    private Button forwardButton;


    @FXML
    public void initialize() {
        String videoSourcePath = "http://127.0.0.1/Komo%20SF%20horizontale.mp4";

        video = new Media(videoSourcePath);

        video.setOnError(() -> {
            // TODO: Log Error
            System.out.println("Error : " + video.getError().toString());
        });

        mediaPlayer = new MediaPlayer(video);

        mediaPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) -> {
            currentTimerLabel.setText(createDurationAsText(newTime));
            mediaProgressBar.setValue(newTime.toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100.0);
        });

        mediaPlayer.setOnReady(() -> {
            Duration videoDuration = video.getDuration();

            totalDurationLabel.setText(createDurationAsText(videoDuration));
            currentTimerLabel.setText(createDurationAsText(mediaPlayer.getStartTime()));
        });

        mediaPlayer.setOnPlaying(() -> {
            playOrPauseIcon.setIcon(FontAwesomeIcon.ICON_PAUSE);
        });

        mediaPlayer.setOnPaused(() -> {
            playOrPauseIcon.setIcon(FontAwesomeIcon.ICON_PLAY);
        });

        mediaPlayer.setOnStopped(() -> {
            playOrPauseIcon.setIcon(FontAwesomeIcon.ICON_PLAY);
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();

            System.out.println(mediaPlayer.getStatus());
            // TODO: Log END OF MEDIA
        });

        mediaPlayer.setOnError(() -> {
            // TODO: Log Error
            System.out.println("Error : " + mediaPlayer.getError().toString());
        });

        mediaView.setMediaPlayer(mediaPlayer);

        mediaView.fitWidthProperty().bind(pageContentContainer.widthProperty().multiply(0.7));
        mediaView.fitHeightProperty().bind(pageContentContainer.heightProperty().multiply(0.6));

        mediaView.setOnError(mediaErrorEvent -> {
            // TODO: Log Error
            System.out.println("Error : " + mediaErrorEvent.getMediaError().toString());
        });

        mediaProgressBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            String newStyle = String.format(Locale.ROOT, "-mediaProgressBar-filled-track-color: " +
                    "linear-gradient(to right, -slider-filled-track-color %1$f%%, -slider-track-color %1$f%%);",
                    newValue.doubleValue());
            mediaProgressBar.setStyle(newStyle);
        });

        mediaProgressBar.setOnMousePressed(mouseEvent -> {
            mediaPlayerStatusBeforeProgressBarPressed = mediaPlayer.getStatus();

            if (mediaPlayer.getStatus() == Status.READY) {
                mediaPlayer.play();
            }

            mediaPlayer.pause();
        });

        mediaProgressBar.setOnMouseDragged(mouseEvent -> {
            Duration newTime = new Duration((mediaProgressBar.getValue() / 100.0) * mediaPlayer.getTotalDuration().toMillis());

            mediaPlayer.seek(newTime);
            currentTimerLabel.setText(createDurationAsText(newTime));
        });

        mediaProgressBar.setOnMouseReleased(mouseEvent -> {
            Duration newTime = new Duration(mediaPlayer.getTotalDuration().multiply(mediaProgressBar.getValue() / 100.0).toMillis());

            mediaPlayer.seek(newTime);

            currentTimerLabel.setText(createDurationAsText(newTime));

            // Handle the case where the video is at the end
            if (newTime.toMillis() == mediaPlayer.getStopTime().toMillis()) {
                mediaPlayer.stop();
            } else {
                if (mediaPlayerStatusBeforeProgressBarPressed == Status.PLAYING) {
                    mediaPlayer.play();
                }

                mediaPlayerStatusBeforeProgressBarPressed = mediaPlayer.getStatus();
            }

        });



        volumeBar.setValue(1.0);
        volumeBar.setMin(0.0);
        volumeBar.setMax(1.0);
        volumeBar.valueProperty().bindBidirectional(mediaPlayer.volumeProperty());

        volumeBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.doubleValue() == 0.0 && newValue.doubleValue() > 0.0) {
                volumeIcon.setIcon(FontAwesomeIcon.ICON_VOLUME_UP);
            } else if (oldValue.doubleValue() > 0.0 && newValue.doubleValue() == 0.0) {
                volumeIcon.setIcon(FontAwesomeIcon.ICON_VOLUME_OFF);
            }

            String newStyle = String.format(Locale.ROOT, "-volumeBar-filled-track-color: " +
                            "linear-gradient(to right, -slider-filled-track-color %1$f%%, -slider-track-color %1$f%%);",
                    newValue.doubleValue() * 100.0);

            volumeBar.setStyle(newStyle);
        });


        volumeButton.setOnAction(actionEvent -> muteOrUnmute());
        playButton.setOnAction(actionEvent -> playOrPauseVideo());
        backwardButton.setOnAction(actionEvent -> goBackwardInVideo());
        forwardButton.setOnAction(actionEvent -> goForwardInVideo());
        returnToPreviousViewButton.setOnAction(actionEvent -> displayLayoutView());
    }

    private void muteOrUnmute() {
        if (mediaPlayer.getVolume() == 0.0) {
            if (volumeBeforeMute > 0.0) {
                mediaPlayer.setVolume(volumeBeforeMute);
            } else {
                mediaPlayer.setVolume(1.0);
            }
            return;
        }

        volumeBeforeMute = mediaPlayer.getVolume();
        mediaPlayer.setVolume(0.0);
    }

    private void playOrPauseVideo() {
        Status status = mediaPlayer.getStatus();

        if (status == Status.UNKNOWN  || status == Status.HALTED) {
            // don't do anything in these states
            return;
        }

        if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
            // Rewind the movie if we're sitting at the end
            if (isVideoAtEnd) {
                mediaPlayer.seek(mediaPlayer.getStartTime());
                isVideoAtEnd = false;
            }
            mediaPlayer.play();
        } else {
            mediaPlayer.pause();
        }
    }

    private void goBackwardInVideo() {
        Status status = mediaPlayer.getStatus();

        if (status == Status.UNKNOWN  || status == Status.HALTED) {
            // don't do anything in these states
            return;
        }

        mediaPlayer.seek(mediaPlayer.getStartTime());
    }

    private void goForwardInVideo() {
        Status status = mediaPlayer.getStatus();

        if (status == Status.UNKNOWN  || status == Status.HALTED) {
            // don't do anything in these states
            return;
        }

        mediaPlayer.stop();
    }

    private String createDurationAsText(Duration duration) {
        StringBuilder stringBuilder = new StringBuilder();

        int seconds = (int)Math.floor(duration.toSeconds());
        int minutes = (int)Math.floor(duration.toMinutes());
        int hours = (int)Math.floor(duration.toHours());

        if (hours != 0) {
            minutes = minutes % 60;
            seconds = (seconds - 60 * minutes) % 3600;

            stringBuilder
                    .append(hours)
                    .append(":")
                    .append(minutes < 10 ? "0" + minutes : minutes)
                    .append(":")
                    .append(seconds < 10 ? "0" + seconds : seconds);
        } else {
            seconds = seconds % 60;

            stringBuilder
                    .append(minutes)
                    .append(":")
                    .append(seconds < 10 ? "0" + seconds : seconds);
        }

        return stringBuilder.toString();
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
