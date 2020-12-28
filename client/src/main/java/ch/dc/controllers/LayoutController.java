package ch.dc.controllers;

import ch.dc.Client;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;

import java.io.IOException;

public class LayoutController {

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
    public void initialize() {
        allFilesButton.setOnAction(event -> {
            displayAllFilesView();
        });

        myFilesButton.setOnAction(event -> {
            displayMyFilesView();
        });

        disconnectButton.setOnAction(event -> {
            disconnectFromServer();
        });
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

    private void resetScrollBar() {
        centerScrollPane.setHvalue(centerScrollPane.getHmin());
        centerScrollPane.setVvalue(centerScrollPane.getVmin());
    }

    private void changeSelectedButton(Button oldSelectedButton, Button newSelectedButton) {
        oldSelectedButton.getStyleClass().removeAll("menuButtonSelected");
        newSelectedButton.getStyleClass().add("menuButtonSelected");
    }
}
