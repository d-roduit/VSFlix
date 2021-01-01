package ch.dc.controllers;

import ch.dc.Client;
import ch.dc.FileType;
import ch.dc.FontAwesome;
import ch.dc.FontAwesomeIcon;
import ch.dc.models.ClientModel;
import ch.dc.viewModels.FileEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;

public class MyFilesController {

    private final ClientModel clientModel = ClientModel.getInstance();
    private final ObservableMap<BorderPane, File> myAudioFilesMap = FXCollections.observableMap(new HashMap<>());
    private final ObservableMap<BorderPane, File> myVideoFilesMap = FXCollections.observableMap(new HashMap<>());

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
        drawFilesEntries(FileType.AUDIO);
        drawFilesEntries(FileType.VIDEO);

        addAudioButton.setOnAction(actionEvent -> addFile(FileType.AUDIO));
        addVideoButton.setOnAction(actionEvent -> addFile(FileType.VIDEO));
    }

    private void addFile(FileType fileType) {
        switch (fileType) {
            case AUDIO:
                File audioFile = getFileWitFileChooser(
                        "Select Audio File",
                        new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3")
                );

                if (audioFile != null) {
                    BorderPane XMLEntry = createXMLFileEntry(audioFile, fileType);
                    FileEntry fileEntry = new FileEntry(audioFile, fileType, XMLEntry);

                    clientModel.getMyAudioFiles().add(fileEntry);
                    drawFilesEntries(FileType.AUDIO);
                }
                break;

            case VIDEO:
                File videoFile = getFileWitFileChooser(
                        "Select Video File",
                        new FileChooser.ExtensionFilter("Video Files", "*.mp4")
                );

                if (videoFile != null) {
                    BorderPane XMLEntry = createXMLFileEntry(videoFile, fileType);
                    FileEntry fileEntry = new FileEntry(videoFile, fileType, XMLEntry);

                    clientModel.getMyVideoFiles().add(fileEntry);
                    drawFilesEntries(FileType.VIDEO);
                }
                break;
        }
    }

    private void unshareFile(BorderPane XMLEntry, FileType fileType) {
        boolean mustUnshare = askUnshareConfirmation();

        if (mustUnshare) {
            Optional<FileEntry> correspondingFileEntry;

            switch (fileType) {
                case AUDIO:
                    correspondingFileEntry = clientModel.getMyAudioFiles()
                            .stream()
                            .filter(fileEntry -> fileEntry.getXMLEntry().equals(XMLEntry))
                            .findFirst();

                    correspondingFileEntry.ifPresent(fileEntry -> {
                        clientModel.getMyAudioFiles().remove(fileEntry);
                    });
                    drawFilesEntries(fileType);
                    break;
                case VIDEO:
                    correspondingFileEntry = clientModel.getMyVideoFiles()
                            .stream()
                            .filter(fileEntry -> fileEntry.getXMLEntry().equals(XMLEntry))
                            .findFirst();

                    correspondingFileEntry.ifPresent(fileEntry -> {
                        clientModel.getMyVideoFiles().remove(fileEntry);
                    });
                    drawFilesEntries(fileType);
                    break;
            }
        }
    }

    private File getFileWitFileChooser(String fileChooserTitle, FileChooser.ExtensionFilter extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(fileChooserTitle);
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Returns a File or null if dialog has been cancelled
        return fileChooser.showOpenDialog((Client.stage != null) ? Client.stage : null);
    }

    private BorderPane createXMLFileEntry(File file, FileType fileType) {
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

        unshareFileButton.setOnAction(actionEvent -> unshareFile(borderPane, fileType));
        unshareFileButton.setGraphic(unshareFileIcon);
        unshareFileButton.setTooltip(unshareFileTooltip);

        unshareFileBox.getChildren().add(unshareFileButton);

        borderPane.setLeft(fileTypeBox);
        borderPane.setCenter(fileTitleBox);
        borderPane.setRight(unshareFileBox);

        borderPane.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    System.out.println("Double clicked");

                }
            }
        });

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

    private void drawFilesEntries(FileType fileType) {
        switch (fileType) {
            case AUDIO:
                audioFilesListBox.getChildren().clear();

                for (FileEntry fileEntry: clientModel.getMyAudioFiles()) {
                    BorderPane XMLEntry = createXMLFileEntry(fileEntry.getFile(), fileType);
                    fileEntry.setXMLEntry(XMLEntry);

                    audioFilesListBox.getChildren().add(XMLEntry);
                }
                break;
            case VIDEO:
                videoFilesListBox.getChildren().clear();

                for (FileEntry fileEntry: clientModel.getMyVideoFiles()) {
                    BorderPane XMLEntry = createXMLFileEntry(fileEntry.getFile(), fileType);
                    fileEntry.setXMLEntry(XMLEntry);

                    videoFilesListBox.getChildren().add(XMLEntry);
                }
                break;
        }
    }

    private boolean askUnshareConfirmation() {
        boolean unshare = false;

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Do you really want to unshare this file ?",
                ButtonType.YES,
                ButtonType.NO
        );

        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

        if (!result.equals(ButtonType.NO)) {
            unshare = true;
        }

        return unshare;
    }
}
