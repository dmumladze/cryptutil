package com.citigroup.icg.cmc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class CsvReportVisitor implements Visitor<ArchiveResults> {
    private Path outputFilePath;
    private final ProgressReporter reporter;

    public CsvReportVisitor(Path outputFilePath, ProgressReporter reporter) {
        this.outputFilePath = outputFilePath;
        this.reporter = reporter;
    }

    @Override
    public void visit(ArchiveResults results) {
        try {
            if (outputFilePath != null) {
                Files.deleteIfExists(outputFilePath);
                Files.createFile(outputFilePath);
            } else {
                outputFilePath = File.createTempFile("cryptutil", ".psv").toPath();
            }

            PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath.toFile().getAbsoluteFile()));
            writer.println("OriginalFileName|ArchivedFileName|DateCreated|FileSize|rror");

            for (ArchiveFileInfo fileInfo : results.getArchivedFiles()) {
                String originalFile = fileInfo.getOriginalFile();
                BasicFileAttributes attributes = Files.readAttributes(Paths.get(originalFile), BasicFileAttributes.class);

                writer.println(String.format("%s|%s|%s|%s|%s", originalFile, fileInfo.getArchivedFile(),
                    attributes.creationTime(),
                    attributes.size(),
                    fileInfo.getExceptionAsString()
                ));
            }
            writer.close();
            reporter.log("Report: %s", outputFilePath.toString());
        } catch (Exception e) {
            reporter.log("Error: %s", e.toString());
        }
    }
}
