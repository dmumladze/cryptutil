package com.citigroup.icg.cmc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class FileTraverser {

    public static List<File> getFiles(String path, Collection<String> excludes) {
        List<File> files = new ArrayList<>();
        collectFiles(path, files, excludes);
        return files;
    }

    private static void collectFiles(String path, List<File> files, Collection<String> excludes) {
        File[] fileList = new File(path).listFiles(pathname -> {
            String ext = pathname.getName().toLowerCase(Locale.ROOT);
            return excludes == null || excludes.stream().noneMatch(ext::endsWith);
        });
        if (fileList == null)
            return;
        for (File file: fileList) {
            if(!file.isDirectory()) {
                files.add(file);
            } else {
                collectFiles(file.getAbsolutePath(), files, excludes);
            }
        }
    }
}
