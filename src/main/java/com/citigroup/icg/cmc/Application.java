package com.citigroup.icg.cmc;

import java.io.File;
import java.util.List;

public class Application {
    /*
        run with input path as folder
        --input-path c:\temp --password abc123 --skip-ext zip,csv,txt --output-file c:\temp\cryptutil.output.csv --test

        run without output file
        --input-path c:\temp --password abc123 --skip-ext zip --test

        run with input file output file
        --input-path c:\temp\input.txt --password abc123 --skip-ext zip --test
    */
    public static void main(String[] args) {
        ConsoleProgressReporter reporter = new ConsoleProgressReporter();
        try {
            ArchiveOptions options = ArchiveOptions.parseHelp(args);
            if (options.isHelp()) {
                options.displayHelp();
                System.exit(0);
            }
            options = ArchiveOptions.parseRequired(args);

            ValidationResults validationResults = options.validate();
            if (!validationResults.isValid()) {
                ValidationResultsPrinter printer = new ValidationResultsPrinter();
                validationResults.accept(printer);
                System.exit(0);
            }

            reporter.log("Looking for files...");
            List<File> files = FileHarvester.harvest(options.getInputPath(), options.getSkipExt());
            if (files.size() == 0)
                throw new Exception("There are no files to encrypt.");
            reporter.log("Found %d files %n", files.size() - 1);

            ArchiveService service = new ArchiveService(options);
            ArchiveResults results = service.runArchiver(files, reporter);
            reporter.complete();

            CsvReportMaker visitor = new CsvReportMaker(options.getOutputFilePath(), reporter);
            results.accept(visitor);
        } catch (Exception e) {
            reporter.log(e);
        }
    }
}
