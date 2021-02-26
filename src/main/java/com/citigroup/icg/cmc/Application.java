package com.citigroup.icg.cmc;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class Application {

    /*
        java -jar cryptutil.jar -path c:\temp -password abc123 -input-file c:\temp\input.txt -output-file c:\temp\output.txt
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

            //TODO #7: read files list from -input-file option
            /*
                if (-input-file option set) {
                    read all lines from -input-file into HashMap (file name must be lowercase)
                } else {
                    use FileTraverser.getFiles
                }
            */
            List<File> files = FileTraverser.getFiles(options.getPath(), options.getExcludes());
            //TODO #8: if 'files' list is empty, throw exception -> 'throw new Exception("There are no file to archive.")

            ArchiveService service = new ArchiveService(options);
            Collection<ArchiveResult> results = service.runArchiver(files, reporter);

            //TODO #9: create CSV file if -input-file is set and create header row
            for (ArchiveResult result : results) {
                for (FileInfo fileInfo : result.getArchivedFiles()) {
                    //TODO #9: add columns 'OriginalFileName', 'ArchivedFileName', 'DateCreated', 'FileSize'
                }
            }
        } catch (Exception e) {
            reporter.log(e.getMessage());
        }
    }
}
