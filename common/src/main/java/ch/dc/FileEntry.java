package ch.dc;

import java.io.File;
import java.io.Serializable;

/**
 * The FileEntry class.
 * Wraps the information needed to be able to stream a file.
 */
public class FileEntry implements Serializable {

    private final File file;
    private final FileType fileType;
    private final String clientIp;
    private final int clientHttpPort;

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
