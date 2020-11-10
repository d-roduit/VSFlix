package ch.dc;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class SecondaryController {

    private Media media;
    private MediaPlayer mediaPlayer;

    @FXML
    private Label mediaDuration;

    @FXML
    private MediaView mediaView;

    @FXML
    private Button secondaryButton;

    @FXML
    private void switchToPrimary() throws IOException {
        secondaryButton.setText("heyy changement de texte");
//        Client.setRoot("primary");

    }

    @FXML
    private void playMedia() {
        mediaPlayer.play();
    }

    @FXML
    private void pauseMedia() {
        mediaPlayer.pause();
    }

    @FXML
    public void initialize() {

        // Files possible :
            // https://droduit.ch/vsflix/lacalin.mp4
//             https://droduit.ch/vsflix/cassius.mp3
            // https://droduit.ch/vsflix/audio.wav
        String path = "https://droduit.ch/vsflix/lacalin.mp4";
//        String path = "http://178.194.94.142:45001/lacalin.mp4";
        //Instantiating Media class
//        Media media = new Media(new File(path).toURI().toString());
        media = new Media(path);
        mediaPlayer = new MediaPlayer(media);

        mediaView.setMediaPlayer(mediaPlayer);

        mediaPlayer.setOnError(() -> System.out.println("Error : " + mediaPlayer.getError().toString()));
        mediaPlayer.setOnReady(new Runnable() {

            @Override
            public void run() {


                mediaDuration.setText("Duration: " + media.getDuration().toSeconds() + " seconds");

                // Display media's metadata in console

                for (Map.Entry<String, Object> entry : media.getMetadata().entrySet()){
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            }
        });

        mediaPlayer.setOnPlaying(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        System.out.println(mediaPlayer.getCurrentTime().toSeconds());
                    }
                }, 0, 1000);
            }
        });
    }
}