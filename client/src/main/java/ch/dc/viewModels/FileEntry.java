package ch.dc.viewModels;

import ch.dc.FileType;

import java.io.File;

public class FileEntry {

    private File file;
    private final FileType fileType;

    public FileEntry(File file, FileType fileType) {
        this.file = file;
        this.fileType = fileType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public FileType getFileType() {
        return fileType;
    }
}
