package ch.dc.controllers;

import ch.dc.*;
import ch.dc.models.ClientHttpServerModel;
import ch.dc.models.ClientModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MyFilesController {

    private final static String viewName = "Layout/MyFiles";
    private final Router router = Router.getInstance();

    private Task<Parent> loadView;

    private final ClientModel clientModel = ClientModel.getInstance();
    private final ClientHttpServerModel clientHttpServerModel = ClientHttpServerModel.getInstance();
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
        router.setCurrentRoute(viewName);

        drawFilesEntries(FileType.AUDIO);
        drawFilesEntries(FileType.VIDEO);

        addAudioButton.setOnAction(actionEvent -> addFile(FileType.AUDIO));
        addVideoButton.setOnAction(actionEvent -> addFile(FileType.VIDEO));
    }

    private void addFile(FileType fileType) {
        String clientHttpServerIp = clientHttpServerModel.getIp();
        int clientHttpServerPort = clientHttpServerModel.getPort();

        switch (fileType) {
            case AUDIO:
                File audioFile = getFileWitFileChooser(
                        "Select Audio File",
                        new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.m4a")
                );

                if (audioFile != null) {
                    FileEntry fileEntry = new FileEntry(
                            audioFile,
                            fileType,
                            clientHttpServerIp,
                            clientHttpServerPort
                    );

                    addFileOnServer(fileEntry);
                }
                break;

            case VIDEO:
                File videoFile = getFileWitFileChooser(
                        "Select Video File",
                        new FileChooser.ExtensionFilter("Video Files", "*.mp4")
                );

                if (videoFile != null) {
                    FileEntry fileEntry = new FileEntry(
                            videoFile,
                            fileType,
                            clientHttpServerIp,
                            clientHttpServerPort
                    );

                    addFileOnServer(fileEntry);
                }
                break;
        }
    }

    private void addFileOnClient(FileEntry fileEntry) {
        FileType fileType = fileEntry.getFileType();

        switch (fileType) {
            case AUDIO:
                clientModel.getMyAudioFiles().add(fileEntry);
                break;

            case VIDEO:
                clientModel.getMyVideoFiles().add(fileEntry);
                break;
        }

        drawFilesEntries(fileType);
    }

    private void addFileOnServer(FileEntry fileEntry) {
        Task<FileEntry> addFileOnServerTask = new Task<FileEntry>() {
            @Override
            public FileEntry call() throws IOException {
            clientModel.getObjOut().writeUTF(Command.ADDFILE.value);
            clientModel.getObjOut().flush();

            clientModel.getObjOut().writeObject(fileEntry);
            clientModel.getObjOut().flush();

            String addFileStatus = clientModel.getObjIn().readUTF();

            //TODO: Log
            System.out.println("addFileStatus : " + addFileStatus);

            return fileEntry;
            }
        };

        addFileOnServerTask.setOnSucceeded(e -> {
            FileEntry fileEntryReturned = addFileOnServerTask.getValue();

            if (fileEntryReturned != null) {
                addFileOnClient(fileEntryReturned);
            }
        });

        addFileOnServerTask.setOnFailed(e -> {
            // TODO: Log
            addFileOnServerTask.getException().printStackTrace();
        });

        Thread thread = new Thread(addFileOnServerTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void unshareFileOnClient(FileEntry fileEntry) {
        FileType fileType = fileEntry.getFileType();

        switch (fileType) {
            case AUDIO:
                clientModel.getMyAudioFiles().remove(fileEntry);
                break;
            case VIDEO:
                clientModel.getMyVideoFiles().remove(fileEntry);
                break;
        }

        drawFilesEntries(fileType);
    }

    private void unshareFileOnServer(FileEntry fileEntry) {
        Task<FileEntry> unshareFileOnServerTask = new Task<FileEntry>() {
            @Override
            public FileEntry call() throws IOException {
            clientModel.getObjOut().writeUTF(Command.UNSHAREFILE.value);
            clientModel.getObjOut().flush();

            clientModel.getObjOut().writeObject(fileEntry);
            clientModel.getObjOut().flush();

            String unshareFileStatus = clientModel.getObjIn().readUTF();

            //TODO: Log
            System.out.println("unshareFileStatus : " + unshareFileStatus);

            return fileEntry;
            }
        };

        unshareFileOnServerTask.setOnSucceeded(e -> {
            FileEntry fileEntryReturned = unshareFileOnServerTask.getValue();

            if (fileEntryReturned != null) {
                unshareFileOnClient(fileEntryReturned);
            }
        });

        unshareFileOnServerTask.setOnFailed(e -> {
            // TODO: Log
            unshareFileOnServerTask.getException().printStackTrace();
        });

        Thread thread = new Thread(unshareFileOnServerTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void unshareFile(FileEntry fileEntry) {
        boolean mustUnshare = askUnshareConfirmation(fileEntry.getFile().getName());

        if (mustUnshare) {
            unshareFileOnServer(fileEntry);
        }
    }

    private File getFileWitFileChooser(String fileChooserTitle, FileChooser.ExtensionFilter extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(fileChooserTitle);
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Returns a File or null if dialog has been cancelled
        return fileChooser.showOpenDialog((Client.stage != null) ? Client.stage : null);
    }

    private void drawFilesEntries(FileType fileType) {
        switch (fileType) {
            case AUDIO:
                audioFilesListBox.getChildren().clear();

                for (FileEntry fileEntry : clientModel.getMyAudioFiles()) {
                    BorderPane XMLEntry = createXMLFileEntry(fileEntry);
                    audioFilesListBox.getChildren().add(XMLEntry);
                }
                break;
            case VIDEO:
                videoFilesListBox.getChildren().clear();

                for (FileEntry fileEntry : clientModel.getMyVideoFiles()) {
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
        unshareFileButton.setUserData(fileEntry);
        unshareFileButton.setOnAction(actionEvent -> unshareFile(fileEntry));

        unshareFileBox.getChildren().add(unshareFileButton);

        borderPane.setLeft(fileTypeBox);
        borderPane.setCenter(fileTitleBox);
        borderPane.setRight(unshareFileBox);
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

    private boolean askUnshareConfirmation(String fileName) {
        boolean unshare = false;

        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Do you really want to unshare the file \"" + fileName + "\" ?",
                ButtonType.YES,
                ButtonType.NO
        );

        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);

        if (!result.equals(ButtonType.NO)) {
            unshare = true;
        }

        return unshare;
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
}