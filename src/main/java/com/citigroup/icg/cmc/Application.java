package com.citigroup.icg.cmc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class Application {
//--input-path c:\temp --password abc123 --skip-ext zip --output-file c:\temp\cryptutil.output.csv --test

    public static void main(String[] args) {
        ConsoleProgressReporter reporter = new ConsoleProgressReporter();
        try {
            ArchiveOptions options = ArchiveOptions.parseHelp(args);
            if (options.isHelp()) {
                options.displayHelp();
                System.exit(0);
            }
            options = ArchiveOptions.parseRequired(args);

            List<File> files = FileHarvester.harvest(options.getInputPath(), options.getSkipExt());
            if (files.size() == 0)
                throw new Exception("There are no files to encrypt.");

            ArchiveService service = new ArchiveService(options);
            ArchiveResults results = service.runArchiver(files, reporter);
            reporter.complete();

            CsvReportVisitor visitor = new CsvReportVisitor(options.getOutputFilePath(), reporter);
            results.accept(visitor);
        } catch (Exception e) {
            reporter.log("Error: %s", e.toString());
        }
    }
}
