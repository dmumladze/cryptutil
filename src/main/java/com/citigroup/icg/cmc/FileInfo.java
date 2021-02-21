package com.citigroup.icg.cmc;

import java.io.File;

public class FileInfo {
    private File originalFile;
    private File archivedFile;
    private Exception error;

    public FileInfo(File originalFile) {
        this.originalFile = originalFile;
    }

    public File getOriginalFile() {
        return originalFile;
    }

    public File getArchivedFile() {
        return archivedFile;
    }

    public void setArchivedFile(File archivedFile) {
        this.archivedFile = archivedFile;
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }
}
