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
    private Task<Integer> getNbConnectedClientsTask;

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
        nbClientsConnectedLabel.setVisible(false);

        String partialViewToLoad = "AllFiles";

        if (router.hasPartialViewRequested()) {
            String partialViewRequested = router.getPartialViewRequested();

            Client.logger.info("Partial view requested (" + partialViewRequested + ").");

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
            Client.logger.info("AllFiles button clicked.");
            displayAllFilesView();
//            updateNbClientsConnected();
        });

        myFilesButton.setOnAction(event -> {
            Client.logger.info("MyFiles button clicked.");
            displayMyFilesView();
//            updateNbClientsConnected();
        });

        disconnectButton.setOnAction(event -> {
            Client.logger.info("Disconnect button clicked.");
            disconnectFromServer();
        });

        serverAddressLabel.setText(clientModel.getServerAddress());
        serverPortLabel.setText(String.valueOf(clientModel.getServerPort()));

//        updateNbClientsConnected();
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
            Client.logger.info("Load AllFiles view succeeded.");
            Parent fxmlContent = loadView.getValue();

            if (fxmlContent != null) {
                centerScrollPane.setContent(fxmlContent);
                resetScrollBar();
                changeSelectedButton(myFilesButton, allFilesButton);
            }
        });

        loadView.setOnFailed(e -> {
            Client.logger.severe("Load AllFiles view failed. (" + loadView.getException().getMessage() + ").");
        });

        loadView.setOnCancelled(e -> {
            Client.logger.warning("Load AllFiles view cancelled.");
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
            Client.logger.info("Load MyFiles view succeeded.");

            Parent fxmlContent = loadView.getValue();

            if (fxmlContent != null) {
                centerScrollPane.setContent(fxmlContent);
                resetScrollBar();
                changeSelectedButton(allFilesButton, myFilesButton);
            }
        });

        loadView.setOnFailed(e -> {
            Client.logger.severe("Load MyFiles view failed (" + loadView.getException().getMessage() + ").");
            loadView.getException().printStackTrace();
        });

        loadView.setOnCancelled(e -> {
            Client.logger.warning("Load MyFiles view cancelled.");
        });

        Thread thread = new Thread(loadView);
        thread.setDaemon(true);
        thread.start();
    }

    private void disconnectFromServer() {
        if (loadView != null && loadView.isRunning()) {
            loadView.cancel();
        }

        ObjectOutputStream objOut = clientModel.getObjOut();
        try {
            Client.logger.info("Sending " + Command.DISCONNECT.value + " command...");
            objOut.writeUTF(Command.DISCONNECT.value);
            objOut.flush();
        } catch (IOException ioException) {
            Client.logger.severe("Sending " + Command.DISCONNECT.value + " command exception (" + ioException.getMessage() + ").");
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
            Client.logger.info("Disconnecting from server succeeded.");

            Parent fxmlContent = loadView.getValue();

            if (fxmlContent != null) {
                clearMyFiles();
                Client.scene.setRoot(fxmlContent);
                resetScrollBar();
            }
        });

        loadView.setOnFailed(e -> {
            Client.logger.severe("Disconnecting from server failed (" + loadView.getException().getMessage() + ").");
        });

        loadView.setOnCancelled(e -> {
            Client.logger.warning("Disconnecting from server cancelled.");
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
        if (getNbConnectedClientsTask != null && getNbConnectedClientsTask.isRunning()) {
            getNbConnectedClientsTask.cancel();
        }

        getNbConnectedClientsTask = new Task<>() {
            @Override
            public Integer call() {
                String nbClientsConnected = "0";

                try {
                    clientModel.getObjOut().writeUTF(Command.GETNBCONNECTEDCLIENTS.value);
                    clientModel.getObjOut().flush();

                    nbClientsConnected = clientModel.getObjIn().readUTF();
                } catch (IOException ioException) {
                    Client.logger.severe("Exception occurred while getting number of connected clients (" + ioException.getMessage() + ")");
                }

                return Integer.valueOf(nbClientsConnected);
            }
        };

        getNbConnectedClientsTask.setOnSucceeded(e -> {
            Client.logger.info("Get number of connected clients succeeded.");

            int nbClientsConnected = getNbConnectedClientsTask.getValue();

            if (nbClientsConnected == 0) {
                nbClientsConnectedLabel.setVisible(false);
                return;
            }

            if (!nbClientsConnectedLabel.isVisible()) {
//                nbClientsConnectedLabel.setVisible(true);
            }

            nbClientsConnectedLabel.setText(String.valueOf(nbClientsConnected));
        });

        getNbConnectedClientsTask.setOnFailed(e -> {
            Client.logger.severe("Get number of connected clients failed (" + getNbConnectedClientsTask.getException().getMessage() + ").");
        });

        Thread thread = new Thread(getNbConnectedClientsTask);
        thread.setDaemon(true);
        thread.start();
    }
}
