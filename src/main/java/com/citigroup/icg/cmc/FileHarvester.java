package com.citigroup.icg.cmc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHarvester {

    public static List<File> harvest(Path path, Collection<String> excludes) throws Exception {
        if (Files.notExists(path))
            throw new Exception(String.format("Input path '%s' does not exist.", path.toString()));

        List<File> files = new ArrayList<>();

        if (Files.isDirectory(path)) {
            collectFiles(path, files, excludes);
        } else {
            List<String> filePaths = Files.readAllLines(path)
                    .stream().map(f -> f.toLowerCase(Locale.ROOT))
                    .distinct()
                    .filter(name -> excludes == null || !excludes.contains(name.substring(name.lastIndexOf(".") + 1)))
                    .collect(Collectors.toList());

            if (filePaths.isEmpty())
                throw new Exception(String.format("Input file '%s' is empty.", path.toString()));

            for (String filePath : filePaths) {
                if (Files.exists(Paths.get(filePath)))
                    files.add(new File(filePath));
            }
        }
        return files;
    }

    private static void collectFiles(Path path, List<File> files, Collection<String> excludes) {
        File[] fileList = path.toFile().listFiles();
        if (fileList == null)
            return;
        for (File file: fileList) {
            if(!file.isDirectory()) {
                if (!Files.isRegularFile(file.toPath()))
                    continue;
                String name = file.getName();
                if (excludes == null || !excludes.contains(name.substring(name.lastIndexOf(".") + 1)))
                    files.add(file);
            } else {
                collectFiles(file.toPath(), files, excludes);
            }
        }
    }
}
