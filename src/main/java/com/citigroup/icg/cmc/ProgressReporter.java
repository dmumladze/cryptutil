package com.citigroup.icg.cmc;

import me.tongfei.progressbar.ProgressBar;

public interface ProgressReporter {
    void init(long max);
    void report(long step);
    void log(String message);
    void log(String message, Object... args);
    void complete();
}
