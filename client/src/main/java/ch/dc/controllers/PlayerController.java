package ch.dc.controllers;

import com.cathive.fonts.fontawesome.FontAwesomeIcon;
import com.cathive.fonts.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.util.Locale;

public class PlayerController {
    public String mediaSourcePath;
    public Media media;
    public MediaPlayer mediaPlayer;
    public MediaView mediaView;
    private boolean isMediaAtEnd = false;
    private MediaPlayer.Status mediaPlayerStatusBeforeProgressBarPressed;
    private double volumeBeforeMute = 1.0;

    @FXML
    public Slider mediaProgressBar;

    @FXML
    public Label currentTimerLabel;

    @FXML
    public Label totalDurationLabel;

    @FXML
    public Button volumeButton;

    @FXML
    public FontAwesomeIconView volumeIcon;

    @FXML
    public Slider volumeBar;

    @FXML
    public Button backwardButton;

    @FXML
    public Button playButton;

    @FXML
    public FontAwesomeIconView playOrPauseIcon;

    @FXML
    public Button forwardButton;

    public void initializePlayer(String mediaSourcePath, MediaView mediaView) {
        this.mediaSourcePath = mediaSourcePath;
        this.mediaView = mediaView;

        media = new Media(mediaSourcePath);

        media.setOnError(() -> {
            // TODO: Log Error
            System.out.println("Error : " + media.getError().toString());
        });

        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.currentTimeProperty().addListener((observable, oldTime, newTime) -> {
            currentTimerLabel.setText(createDurationAsText(newTime));
            mediaProgressBar.setValue(newTime.toMillis() / mediaPlayer.getTotalDuration().toMillis() * 100.0);
        });

        mediaPlayer.setOnReady(() -> {
            Duration videoDuration = media.getDuration();

            totalDurationLabel.setText(createDurationAsText(videoDuration));
            currentTimerLabel.setText(createDurationAsText(mediaPlayer.getStartTime()));
        });

        mediaPlayer.setOnPlaying(() -> playOrPauseIcon.setIcon(FontAwesomeIcon.ICON_PAUSE));
        mediaPlayer.setOnPaused(() -> playOrPauseIcon.setIcon(FontAwesomeIcon.ICON_PLAY));
        mediaPlayer.setOnStopped(() -> playOrPauseIcon.setIcon(FontAwesomeIcon.ICON_PLAY));

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
        mediaView.setOnMouseClicked(mouseEvent -> playOrPause());
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

            if (mediaPlayer.getStatus() == MediaPlayer.Status.READY) {
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
                if (mediaPlayerStatusBeforeProgressBarPressed == MediaPlayer.Status.PLAYING) {
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
        playButton.setOnAction(actionEvent -> playOrPause());
        backwardButton.setOnAction(actionEvent -> goBackward());
        forwardButton.setOnAction(actionEvent -> goForward());
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

    private void playOrPause() {
        MediaPlayer.Status status = mediaPlayer.getStatus();

        if (status == MediaPlayer.Status.UNKNOWN  || status == MediaPlayer.Status.HALTED) {
            // don't do anything in these states
            return;
        }

        if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY || status == MediaPlayer.Status.STOPPED) {
            // Rewind the movie if we're sitting at the end
            if (isMediaAtEnd) {
                mediaPlayer.seek(mediaPlayer.getStartTime());
                isMediaAtEnd = false;
            }
            mediaPlayer.play();
        } else {
            mediaPlayer.pause();
        }
    }

    private void goBackward() {
        MediaPlayer.Status status = mediaPlayer.getStatus();

        if (status == MediaPlayer.Status.UNKNOWN  || status == MediaPlayer.Status.HALTED) {
            // don't do anything in these states
            return;
        }

        mediaPlayer.seek(mediaPlayer.getStartTime());
    }

    private void goForward() {
        MediaPlayer.Status status = mediaPlayer.getStatus();

        if (status == MediaPlayer.Status.UNKNOWN  || status == MediaPlayer.Status.HALTED) {
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
}
