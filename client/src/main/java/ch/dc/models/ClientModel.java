package ch.dc.models;

import ch.dc.FileEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class ClientModel {

    private final static ClientModel INSTANCE = new ClientModel();

    private String serverAddress;
    private int serverPort;
    private Socket clientSocket;
    private String ip;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    private FileEntry fileToPlay = null;
    private final ObservableList<FileEntry> myAudioFiles = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<FileEntry> myVideoFiles = FXCollections.observableList(new ArrayList<>());

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

    public void setClientSocket(Socket clientSocket) { this.clientSocket = clientSocket; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public ObjectOutputStream getObjOut() {
        return objOut;
    }
    public void setObjOut(ObjectOutputStream objOut) {
        this.objOut = objOut;
    }

    public ObjectInputStream getObjIn() {
        return objIn;
    }
    public void setObjIn(ObjectInputStream objIn) {
        this.objIn = objIn;
    }

    public String getServerAddress() {
        return serverAddress;
    }
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
