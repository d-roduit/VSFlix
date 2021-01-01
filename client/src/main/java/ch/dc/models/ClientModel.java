package ch.dc.models;

import java.util.ArrayList;

public class ClientModel {

    private final static ClientModel INSTANCE = new ClientModel();
    private final ArrayList<String> myAudioFiles = new ArrayList<>();
    private final ArrayList<String> myVideoFiles = new ArrayList<>();
    private final ArrayList<String> availableAudioFiles = new ArrayList<>();
    private final ArrayList<String> availableVideoFiles = new ArrayList<>();

    private ClientModel() {}

    public static ClientModel getInstance() { return INSTANCE; }

    public ArrayList<String> getMyAudioFiles() {
        return myAudioFiles;
    }

    public ArrayList<String> getMyVideoFiles() {
        return myVideoFiles;
    }

    public ArrayList<String> getAvailableAudioFiles() {
        return availableAudioFiles;
    }

    public ArrayList<String> getAvailableVideoFiles() {
        return availableVideoFiles;
    }

}
