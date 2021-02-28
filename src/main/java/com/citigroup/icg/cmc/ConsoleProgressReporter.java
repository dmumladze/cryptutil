package com.citigroup.icg.cmc;

import me.tongfei.progressbar.ProgressBar;

public class ConsoleProgressReporter implements ProgressReporter {
    private ProgressBar progress;

    public void init(long max) {
        this.progress = new ProgressBar("Archiving...", max);
    }

    public void report(long step) {
        progress.stepBy(step);
    }

    public void log(String message) {
        System.out.println(message);
    }

    public void log(String message, Object... args) {
        System.out.printf(message, args);
    }

    public void complete() {
        this.progress.close();
    }
}
