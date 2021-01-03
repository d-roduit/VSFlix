package ch.dc.models;

import ch.dc.viewModels.FileEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientModel {

    private final static ClientModel INSTANCE = new ClientModel();

    private Socket clientSocket;
    private PrintWriter pOut;
    private BufferedReader bIn;

    private FileEntry fileToPlay = null;
    private final ObservableList<FileEntry> myAudioFiles = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<FileEntry> myVideoFiles = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<FileEntry> availableAudioFiles = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<FileEntry> availableVideoFiles = FXCollections.observableList(new ArrayList<>());

    private ClientModel() { }

    public static ClientModel getInstance() { return INSTANCE; }

    public FileEntry getFileToPlay() {
        return fileToPlay;
    }

    public void setFileToPlay(FileEntry fileToPlay) {
        this.fileToPlay = fileToPlay;
    }

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

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public PrintWriter getPOut() {
        return pOut;
    }

    public void setPOut(PrintWriter pOut) {
        this.pOut = pOut;
    }

    public BufferedReader getBIn() {
        return bIn;
    }

    public void setBIn(BufferedReader bIn) {
        this.bIn = bIn;
    }
}
