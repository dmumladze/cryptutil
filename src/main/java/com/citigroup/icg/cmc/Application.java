package com.citigroup.icg.cmc;

public class Application {

    public static void main(String[] args) {
        try {
            ArchiveOptions options = ArchiveOptions.parseHelp(args);
            if (options.isHelp()) {
                options.displayHelp();
                System.exit(0);
            }

            options = ArchiveOptions.parseRequired(args);
            ConsoleProgressReporter reporter = new ConsoleProgressReporter();

            ArchiveService service = new ArchiveService(options);
            service.runArchiver(reporter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
