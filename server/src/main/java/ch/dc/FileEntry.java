package main.java.ch.dc;

import java.io.File;

public class FileEntry {

    private File file;
    private FileType fileType;
    private String clientIp;
    private int clientHttpPort;

    public FileEntry(File file, FileType fileType, String clientIp, int clientHttpPort){
        this.file = file;
        this.fileType = fileType;
        this.clientIp = clientIp;
        this.clientHttpPort = clientHttpPort;
    }

    public File getFile() {
        return file;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getClientIp() {
        return clientIp;
    }

    public int getClientHttpPort() {
        return clientHttpPort;
    }
}
