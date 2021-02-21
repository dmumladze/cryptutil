package com.citigroup.icg.cmc;

import java.util.ArrayList;

public class ArchiveResult {
    private final ArrayList<FileInfo> archivedFiles;

    public ArchiveResult() {
        this.archivedFiles = new ArrayList<>();
    }

    public void addFileInfo(FileInfo fileInfo) {
        this.archivedFiles.add(fileInfo);
    }
}
