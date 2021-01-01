package ch.dc.models;

import ch.dc.viewModels.FileEntry;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;

public class ClientModel {

    private final static ClientModel INSTANCE = new ClientModel();
    private final ObservableList<FileEntry> myAudioFiles = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<FileEntry> myVideoFiles = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<FileEntry> availableAudioFiles = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<FileEntry> availableVideoFiles = FXCollections.observableList(new ArrayList<>());

    private ClientModel() {
        myVideoFiles.addListener((ListChangeListener<? super FileEntry>) (change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    System.out.println("A video was added to the list ! ");
                } else if (change.wasRemoved()) {
                    System.out.println("A video was removed from the list ! ");
                }
            }

            System.out.println("---- LIST OF FILES ----");
            for (FileEntry videoFile: myVideoFiles) {
                System.out.println(videoFile.getFile().getName());
            }
        });
    }

    public static ClientModel getInstance() { return INSTANCE; }

    public ObservableList<FileEntry> getMyAudioFiles() {
        return myAudioFiles;
    }

    public ObservableList<FileEntry> getMyVideoFiles() {
        return myVideoFiles;
    }

    public ObservableList<FileEntry> getAvailableAudioFiles() {
        return availableAudioFiles;
    }

    public ObservableList<FileEntry> getAvailableVideoFiles() {
        return availableVideoFiles;
    }

}
