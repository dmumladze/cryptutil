package com.citigroup.icg.cmc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FileHarvester {

    public static List<File> harvest(Path path, Collection<String> excludeExtensions, Integer skipOlderThanDays) throws Exception {
        if (Files.notExists(path))
            throw new Exception(String.format("Input path '%s' does not exist.", path.toString()));

        Instant maxModifiedInstant = Instant.now().minus(Duration.ofDays(skipOlderThanDays));
        List<File> files = new ArrayList<>();

        if (Files.isDirectory(path)) {
            collectFiles(path, files, excludeExtensions, skipOlderThanDays, maxModifiedInstant);
        } else {
            List<String> filePaths = Files.readAllLines(path)
                    .stream().map(f -> f.toLowerCase(Locale.ROOT))
                    .distinct()
                    .filter(name -> excludeExtensions == null || !excludeExtensions.contains(name.substring(name.lastIndexOf(".") + 1)))
                    .collect(Collectors.toList());

            if (filePaths.isEmpty())
                throw new Exception(String.format("Input file '%s' is empty.", path.toString()));

            for (String filePath : filePaths) {
                if (Files.exists(Paths.get(filePath))) {
                    File file = new File(filePath);
                    if (skipOlderThanDays == 0 || checkLastModifiedDate(file, maxModifiedInstant))
                        files.add(file);
                }
            }
        }
        return files;
    }

    private static void collectFiles(Path path, List<File> files, Collection<String> excludeExtensions,
                                     Integer skipOlderThanDays, Instant maxModifiedInstant) throws IOException {
        File[] fileList = path.toFile().listFiles();
        if (fileList == null)
            return;
        for (File file: fileList) {
            if(!file.isDirectory()) {
                String name = file.getName();
                if (excludeExtensions != null && excludeExtensions.contains(name.substring(name.lastIndexOf(".") + 1)))
                    continue;
                if (skipOlderThanDays == 0 || checkLastModifiedDate(file, maxModifiedInstant))
                    files.add(file);
            } else {
                collectFiles(file.toPath(), files, excludeExtensions, skipOlderThanDays, maxModifiedInstant);
            }
        }
    }

    private static boolean checkLastModifiedDate(File file, Instant maxModifiedInstant) throws IOException {
        Instant fileInstant = Files.getLastModifiedTime(file.toPath()).toInstant();
        Duration duration = Duration.between(fileInstant, maxModifiedInstant);
        return duration.toDays() < 0;
    }
}
