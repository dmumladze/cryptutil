package m0bydk.cryptutil;

public interface ProgressReporter {
    void init(long max);
    void report(long step);
    void log(String message);
    void log(String message, Object... args);
    void log(Exception e);
    void complete();
}
