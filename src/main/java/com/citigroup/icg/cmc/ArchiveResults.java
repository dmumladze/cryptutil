package com.citigroup.icg.cmc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ArchiveResults {
    private final Collection<ArchiveFileInfo> archivedFiles;

    public ArchiveResults() {
        this.archivedFiles = new ArrayList<>();
    }

    public void addArchivedFile(ArchiveFileInfo fileInfo) {
        this.archivedFiles.add(fileInfo);
    }

    public Collection<ArchiveFileInfo> getArchivedFiles() {
        return Collections.unmodifiableCollection(this.archivedFiles);
    }

    public void accept(Visitor<ArchiveResults> visitor) throws Exception {
        visitor.visit(this);
    }
}
