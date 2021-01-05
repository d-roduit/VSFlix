package ch.dc.controllers;

import ch.dc.*;
import ch.dc.models.ClientModel;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class AllFilesController {



    private final static String viewName = "Layout/AllFiles";
    private final Router router = Router.getInstance();

    private Task<Parent> loadView;
    Task<List<FileEntry>> getAvailableFileTask;

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
        Client.logger.info("Drawing available files...");

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
            Client.logger.info("Loading AudioPlayer view succeeded");

            Parent fxmlContent = loadView.getValue();

            if (fxmlContent != null) {
                Client.scene.setRoot(fxmlContent);
            }
        });

        loadView.setOnFailed(e -> {
            Client.logger.severe("Loading AudioPlayer view failed (" + loadView.getException().getMessage() + ").");
            loadView.getException().printStackTrace();
        });

        loadView.setOnCancelled(e -> {
            Client.logger.warning("Loading AudioPlayer view cancelled.");
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
            Client.logger.info("Loading VideoPlayer view succeeded");

            Parent fxmlContent = loadView.getValue();

            if (fxmlContent != null) {
                Client.scene.setRoot(fxmlContent);
            }
        });

        loadView.setOnFailed(e -> {
            Client.logger.severe("Loading VideoPlayer view failed (" + loadView.getException().getMessage() + ").");
        });

        loadView.setOnCancelled(e -> {
            Client.logger.warning("Loading VideoPlayer view cancelled.");
        });

        Thread thread = new Thread(loadView);
        thread.setDaemon(true);
        thread.start();
    }

    private void getAvailableFilesAndDraw() {
        if (getAvailableFileTask != null && getAvailableFileTask.isRunning()) {
            getAvailableFileTask.cancel();
        }

        getAvailableFileTask = new Task<List<FileEntry>>() {
            @Override
            public List<FileEntry> call() throws IOException {
                Client.logger.info("Sending " + Command.GETALLFILES.value + " command...");

                ObjectOutputStream objOut = clientModel.getObjOut();
                objOut.writeUTF(Command.GETALLFILES.value);
                objOut.flush();

                List<FileEntry> availableFileEntries = null;

                try {
                    availableFileEntries = (List<FileEntry>) clientModel.getObjIn().readObject();
                } catch (ClassNotFoundException e) {
                    Client.logger.severe("Could not read object from ObjectInputStream (" + e.getMessage() + ").");
                }

                return availableFileEntries;
            }
        };

        getAvailableFileTask.setOnSucceeded(e -> {
            Client.logger.info("Fetch available files succeeded.");
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
            Client.logger.severe("Fetch available files failed (" + getAvailableFileTask.getException().getMessage() + ").");
        });

        getAvailableFileTask.setOnCancelled(e -> {
            Client.logger.warning("Fetch available files cancelled.");
        });

        Thread thread = new Thread(getAvailableFileTask);
        thread.setDaemon(true);
        thread.start();
    }
}
