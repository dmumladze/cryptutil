package com.citigroup.icg.cmc;

import java.util.ArrayList;
import java.util.Collection;

public class ArchiveResult {
    private final ArrayList<FileInfo> archivedFiles;

    public ArchiveResult() {
        this.archivedFiles = new ArrayList<>();
    }

    public void addArchivedFile(FileInfo fileInfo) {
        this.archivedFiles.add(fileInfo);
    }

    public Collection<FileInfo> getArchivedFiles() {
        return this.archivedFiles;
    }
}
