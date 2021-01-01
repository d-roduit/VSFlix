package ch.dc.controllers;

import ch.dc.Client;
import ch.dc.FontAwesome;
import ch.dc.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class MyFilesController {

    @FXML
    private VBox audioFilesListBox;

    @FXML
    private Button addAudioButton;

    @FXML
    private VBox videoFilesListBox;

    @FXML
    private Button addVideoButton;

    @FXML
    public void initialize() {

        addAudioButton.setOnAction(actionEvent -> addAudioFile());
        addVideoButton.setOnAction(actionEvent -> addVideoFile());
    }

    private void addAudioFile() {
        File audioFile = getFileWitFileChooser(
                "Select Audio File",
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3")
        );

        if (audioFile != null) {
            BorderPane guiAudioFileEntry = createGUIFileEntry(audioFile, FileType.AUDIO);

            audioFilesListBox.getChildren().add(guiAudioFileEntry);
        }
    }

    private void addVideoFile() {
        File videoFile = getFileWitFileChooser(
                "Select Video File",
                new FileChooser.ExtensionFilter("Video Files", "*.mp4")
        );
    }

    private File getFileWitFileChooser(String fileChooserTitle, FileChooser.ExtensionFilter extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(fileChooserTitle);
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Returns a File or null if dialog has been cancelled
        return fileChooser.showOpenDialog((Client.stage != null) ? Client.stage : null);
    }

    private BorderPane createGUIFileEntry(File file, FileType fileType) {
        BorderPane borderPane = new BorderPane();

        HBox fileTypeBox = new HBox();
        fileTypeBox.getStyleClass().add("fileTypeBox");

        FontAwesomeIcon fileTypeIcon = getFileTypeIcon(fileType);

        HBox fileTitleBox = new HBox();
        fileTitleBox.getStyleClass().add("fileTitleBox");

        Label fileTitleLabel = new Label(file.getName());

        HBox unshareFileBox = new HBox();
        unshareFileBox.getStyleClass().add("unshareFileBox");

        Button unshareFileButton = new Button();
        unshareFileButton.getStyleClass().add("unshareFileButton");
        FontAwesomeIcon unshareFileIcon = new FontAwesomeIcon(FontAwesome.UNLINK);
        Tooltip unshareFileTooltip = new Tooltip("Unshare");

        fileTypeBox.getChildren().add(fileTypeIcon);

        fileTitleBox.getChildren().add(fileTitleLabel);

        unshareFileButton.setGraphic(unshareFileIcon);
        unshareFileButton.setTooltip(unshareFileTooltip);

        unshareFileBox.getChildren().add(unshareFileButton);

        borderPane.setLeft(fileTypeBox);
        borderPane.setCenter(fileTitleBox);
        borderPane.setRight(unshareFileBox);

        return borderPane;
    }

    private FontAwesomeIcon getFileTypeIcon(FileType fileType) {
        FontAwesomeIcon icon = new FontAwesomeIcon(FontAwesome.FILE);

        switch (fileType) {
            case AUDIO:
                icon.setIcon(FontAwesome.MUSIC);
                break;
            case VIDEO:
                icon.setIcon(FontAwesome.VIDEO);
                break;
        }

        return icon;
    }

    private enum FileType {
        AUDIO,
        VIDEO
    }
}
