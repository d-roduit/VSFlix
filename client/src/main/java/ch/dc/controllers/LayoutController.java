package ch.dc.controllers;

import ch.dc.Client;
import ch.dc.Command;
import ch.dc.Router;
import ch.dc.models.ClientModel;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class LayoutController {

    private final ClientModel clientModel = ClientModel.getInstance();
    private final Router router = Router.getInstance();

    private Task<Parent> loadView;

    @FXML
    private ScrollPane centerScrollPane;

    @FXML
    private Button allFilesButton;

    @FXML
    public Button myFilesButton;

    @FXML
    public Button disconnectButton;

    @FXML
    private Label serverAddressLabel;

    @FXML
    private Label serverPortLabel;

    @FXML
    private Label nbClientsConnectedLabel;

    @FXML
    public void initialize() {
        String partialViewToLoad = "AllFiles";

        if (router.hasPartialViewRequested()) {
            String partialViewRequested = router.getPartialViewRequested();

            // TODO: Log has partial view requested : partialView value

            if (partialViewRequested.equals("MyFiles")) {
                partialViewToLoad = partialViewRequested;
            }
        }

        if (partialViewToLoad.equals("MyFiles")) {
            displayMyFilesView();
        } else {
            displayAllFilesView();
        }


        allFilesButton.setOnAction(event -> {
            displayAllFilesView();
            updateNbClientsConnected();
        });

        myFilesButton.setOnAction(event -> {
            displayMyFilesView();
            updateNbClientsConnected();
        });

        disconnectButton.setOnAction(event -> {
            disconnectFromServer();
        });

        serverAddressLabel.setText(clientModel.getServerAddress());
        serverPortLabel.setText(String.valueOf(clientModel.getServerPort()));

        updateNbClientsConnected();
    }

    private void displayAllFilesView() {
        if (loadView != null && loadView.isRunning()) {
            loadView.cancel();
        }

        loadView = new Task<Parent>() {
            @Override
            public Parent call() throws IOException {
                Parent fxmlContent = Client.loadFXML("AllFiles");

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
                centerScrollPane.setContent(fxmlContent);
                resetScrollBar();
                changeSelectedButton(myFilesButton, allFilesButton);
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

    private void displayMyFilesView() {
        if (loadView != null && loadView.isRunning()) {
            loadView.cancel();
        }

        loadView = new Task<Parent>() {
            @Override
            public Parent call() throws IOException {
                Parent fxmlContent = Client.loadFXML("MyFiles");

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
                centerScrollPane.setContent(fxmlContent);
                resetScrollBar();
                changeSelectedButton(allFilesButton, myFilesButton);
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

    private void disconnectFromServer() {
        if (loadView != null && loadView.isRunning()) {
            loadView.cancel();
        }

        // TODO: Disconnect from server
        ObjectOutputStream objOut = clientModel.getObjOut();
        try {
            objOut.writeUTF(Command.DISCONNECT.value);
            objOut.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // Display the Connection view
        loadView = new Task<Parent>() {
            @Override
            public Parent call() throws IOException {
                Parent fxmlContent = Client.loadFXML("Connection");

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
                clearMyFiles();
                Client.scene.setRoot(fxmlContent);
                resetScrollBar();
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

    private void clearMyFiles() {
        clientModel.getMyAudioFiles().clear();
        clientModel.getMyVideoFiles().clear();
    }

    private void resetScrollBar() {
        centerScrollPane.setHvalue(centerScrollPane.getHmin());
        centerScrollPane.setVvalue(centerScrollPane.getVmin());
    }

    private void changeSelectedButton(Button oldSelectedButton, Button newSelectedButton) {
        oldSelectedButton.getStyleClass().removeAll("menuButtonSelected");
        newSelectedButton.getStyleClass().add("menuButtonSelected");
    }

    private void updateNbClientsConnected() {
        Task<Integer> getNbConnectedClientsTask = new Task<>() {
            @Override
            public Integer call() {
                int nbClientsConnected = 0;

                try {
                    clientModel.getObjOut().writeUTF(Command.GETNBCONNECTEDCLIENTS.value);
                    clientModel.getObjOut().flush();

                    nbClientsConnected = clientModel.getObjIn().readInt();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                return nbClientsConnected;
            }
        };

        getNbConnectedClientsTask.setOnSucceeded(e -> {
            int nbClientsConnected = getNbConnectedClientsTask.getValue();

            nbClientsConnectedLabel.setText(String.valueOf(nbClientsConnected));
        });

        getNbConnectedClientsTask.setOnFailed(e -> {
            // TODO: Log error with logger
            getNbConnectedClientsTask.getException().printStackTrace();
        });

        Thread thread = new Thread(getNbConnectedClientsTask);
        thread.setDaemon(true);
        thread.start();
    }
}
