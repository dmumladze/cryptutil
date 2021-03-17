package m0bydk.cryptutil;

public class ArchiveFileInfo {
    private String originalFile;
    private String archivedFile;
    private Exception exception;

    public ArchiveFileInfo(String originalFile) {
        this.originalFile = originalFile;
    }

    public String getOriginalFile() {
        return originalFile;
    }

    public String getArchivedFile() {
        return archivedFile != null ? archivedFile : "";
    }

    public void setArchivedFile(String archivedFile) {
        this.archivedFile = archivedFile;
    }

    public Exception getException() {
        return exception;
    }

    public String getExceptionAsString() {
        if (exception == null)
            return "";
        else
            return exception.toString();
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
