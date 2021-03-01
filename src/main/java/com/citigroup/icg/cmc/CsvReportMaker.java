package com.citigroup.icg.cmc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class CsvReportMaker implements Visitor<ArchiveResults> {
    private Path outputFilePath;
    private final ProgressReporter reporter;

    public CsvReportMaker(Path outputFilePath, ProgressReporter reporter) {
        this.outputFilePath = outputFilePath;
        this.reporter = reporter;
    }

    @Override
    public void visit(ArchiveResults results) {
        reporter.log("Generating report...");
        try {
            if (outputFilePath != null) {
                Files.deleteIfExists(outputFilePath);
                Files.createFile(outputFilePath);
            } else {
                outputFilePath = File.createTempFile("cryptutil", ".psv").toPath();
            }

            PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath.toFile().getAbsoluteFile()));
            writer.println("OriginalFileName|ArchivedFileName|DateCreated|FileSize|Error");

            for (ArchiveFileInfo fileInfo : results.getArchivedFiles()) {
                String originalFile = fileInfo.getOriginalFile();
                String archivedFile = fileInfo.getArchivedFile();

                BasicFileAttributes attributes = null;
                if (Files.exists(Paths.get(archivedFile))) {
                    attributes = Files.readAttributes(Paths.get(archivedFile), BasicFileAttributes.class);
                } else if (Files.exists(Paths.get(originalFile))) {
                    attributes = Files.readAttributes(Paths.get(originalFile), BasicFileAttributes.class);
                }

                if (attributes != null) {
                    writer.println(String.format("%s|%s|%s|%s|%s", originalFile, archivedFile,
                            attributes.creationTime(),
                            attributes.size(),
                            fileInfo.getExceptionAsString()));
                } else {
                    writer.println(String.format("%s|%s|%s|%s|%s", originalFile, archivedFile,
                            "", "", fileInfo.getExceptionAsString()));
                }
            }
            writer.close();
            reporter.log("Report: %s", outputFilePath.toString());
        } catch (Exception e) {
            reporter.log(e);
        }
    }
}
