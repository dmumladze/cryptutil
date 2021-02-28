package com.citigroup.icg.cmc;

import me.tongfei.progressbar.ProgressBar;

import java.util.Arrays;

public class ConsoleProgressReporter implements ProgressReporter {
    private ProgressBar progress;

    public void init(long max) {
        if (this.progress != null) {
            this.progress.close();
        }
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

    public void log(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Error: ")
          .append(e.getMessage())
          .append(System.lineSeparator())
          .append(Arrays.toString(e.getStackTrace()));
        System.out.println(sb.toString());
    }

    public void complete() {
        this.progress.close();
    }
}
