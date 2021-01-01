package ch.dc.viewModels;

import ch.dc.FileType;
import javafx.scene.layout.Pane;

import java.io.File;

public class FileEntry {

    private File file;
    private final FileType fileType;
    private Pane XMLEntry;

    public FileEntry(File file, FileType fileType, Pane XMLEntry) {
        this.file = file;
        this.fileType = fileType;
        this.XMLEntry = XMLEntry;
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

    public Pane getXMLEntry() {
        return XMLEntry;
    }

    public void setXMLEntry(Pane XMLEntry) {
        this.XMLEntry = XMLEntry;
    }
}
