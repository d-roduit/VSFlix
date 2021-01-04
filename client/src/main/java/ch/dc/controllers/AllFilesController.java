package ch.dc.controllers;

import ch.dc.*;
import ch.dc.models.ClientModel;
import ch.dc.FileEntry;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class AllFilesController {



    private final static String viewName = "Layout/AllFiles";
    private final Router router = Router.getInstance();

    private Task<Parent> loadView;

    private final ClientModel clientModel = ClientModel.getInstance();

    @FXML
    private VBox audioFilesListBox;

    @FXML
    private VBox videoFilesListBox;

    @FXML
    public void initialize() {
        router.setCurrentRoute(viewName);

        getAvailableFilesAndDraw();
    }

    private void drawAvailableFilesEntries(List<FileEntry> fileEntries, FileType fileType) {
        switch (fileType) {
            case AUDIO:
                audioFilesListBox.getChildren().clear();

                for (FileEntry fileEntry : fileEntries) {
                    BorderPane XMLEntry = createXMLFileEntry(fileEntry);
                    audioFilesListBox.getChildren().add(XMLEntry);
                }
                break;
            case VIDEO:
                videoFilesListBox.getChildren().clear();

                for (FileEntry fileEntry : fileEntries) {
                    BorderPane XMLEntry = createXMLFileEntry(fileEntry);
                    videoFilesListBox.getChildren().add(XMLEntry);
                }
                break;
        }
    }

    private BorderPane createXMLFileEntry(FileEntry fileEntry) {
        BorderPane borderPane = new BorderPane();

        HBox fileTypeBox = new HBox();
        fileTypeBox.getStyleClass().add("fileTypeBox");

        FontAwesomeIcon fileTypeIcon = getFileTypeIcon(fileEntry.getFileType());

        HBox fileTitleBox = new HBox();
        fileTitleBox.getStyleClass().add("fileTitleBox");

        Label fileTitleLabel = new Label(fileEntry.getFile().getName());

        fileTypeBox.getChildren().add(fileTypeIcon);

        fileTitleBox.getChildren().add(fileTitleLabel);

        borderPane.setLeft(fileTypeBox);
        borderPane.setCenter(fileTitleBox);
        borderPane.setUserData(fileEntry);
        borderPane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {
                    openFileInPlayer(fileEntry);
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

    private void openFileInPlayer(FileEntry fileEntry) {
        clientModel.setFileToPlay(fileEntry);

        switch (fileEntry.getFileType()) {
            case AUDIO:
                displayAudioPlayerView();
                break;
            case VIDEO:
                displayVideoPlayerView();
                break;
        }
    }

    private void displayAudioPlayerView() {
        if (loadView != null && loadView.isRunning()) {
            loadView.cancel();
        }

        loadView = new Task<Parent>() {
            @Override
            public Parent call() throws IOException {
                Parent fxmlContent = Client.loadFXML("AudioPlayer");

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

    private void displayVideoPlayerView() {
        if (loadView != null && loadView.isRunning()) {
            loadView.cancel();
        }

        loadView = new Task<Parent>() {
            @Override
            public Parent call() throws IOException {
                Parent fxmlContent = Client.loadFXML("VideoPlayer");

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

    private void getAvailableFilesAndDraw() {
        Task<List<FileEntry>> getAvailableFileTask = new Task<List<FileEntry>>() {
            @Override
            public List<FileEntry> call() throws IOException {
                clientModel.getObjOut().writeUTF(Command.GETALLFILES.value);
                clientModel.getObjOut().flush();

                List<FileEntry> availableFileEntries = null;

                try {
                    availableFileEntries = (List<FileEntry>) clientModel.getObjIn().readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                //TODO: Log

                return availableFileEntries;
            }
        };

        getAvailableFileTask.setOnSucceeded(e -> {
            List<FileEntry> availableFileEntries = getAvailableFileTask.getValue();

            if (availableFileEntries != null) {
                List<FileEntry> availableAudioFiles = availableFileEntries
                        .stream()
                        .filter(fileEntry -> fileEntry.getFileType() == FileType.AUDIO)
                        .collect(Collectors.toList());

                List<FileEntry> availableVideoFiles = availableFileEntries
                        .stream()
                        .filter(fileEntry -> fileEntry.getFileType() == FileType.VIDEO)
                        .collect(Collectors.toList());

                drawAvailableFilesEntries(availableAudioFiles, FileType.AUDIO);
                drawAvailableFilesEntries(availableVideoFiles, FileType.VIDEO);
            }
        });

        getAvailableFileTask.setOnFailed(e -> {
            // TODO: Log
            getAvailableFileTask.getException().printStackTrace();
        });

        Thread thread = new Thread(getAvailableFileTask);
        thread.setDaemon(true);
        thread.start();
    }
}
