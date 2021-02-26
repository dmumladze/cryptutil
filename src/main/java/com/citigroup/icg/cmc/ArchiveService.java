package com.citigroup.icg.cmc;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class ArchiveService {
    private FileStorage storage;
    private final ArchiveOptions options;

    public ArchiveService(ArchiveOptions options) {
        this.options = options;
    }

    public Collection<ArchiveResult> runArchiver(List<File> files, ProgressReporter reporter) {
        this.storage = new FileStorage(files);

        reporter.log("Found %d files %n", files.size() - 1);
        reporter.init(files.size() - 1);

        List<Callable<ArchiveResult>> tasks = new ArrayList<>();
        //use only 70% of available processors
        int processorCount = (int) Math.round((Runtime.getRuntime().availableProcessors() / 100.0) * 70);

        for (int i = 0; i <= processorCount; i++) {
            tasks.add(() -> archiverTask(reporter));
        }

        ExecutorService exec = Executors.newFixedThreadPool(processorCount);
        Collection<ArchiveResult> results = new ArrayList<>(processorCount);
        try {
            List<Future<ArchiveResult>> futureResults = exec.invokeAll(tasks);
            for (Future<ArchiveResult> result : futureResults) {
                results.add(result.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            reporter.log(e.getMessage());
        } finally {
            exec.shutdown();
        }
        return results;
    }

    private ArchiveResult archiverTask(ProgressReporter reporter) {
        ArchiveResult result = new ArchiveResult();
        while (true) {
            File file = this.storage.get();
            if (file == null) {
                break;
            }
            FileInfo fileInfo = new FileInfo(file);
            try {
                File zipFile = archiveFile(fileInfo);
                if (this.options.isMeasure()) {
                    //delete file if program running with -test option
                    Files.deleteIfExists(Paths.get(zipFile.getAbsolutePath()));
                } else {
                    fileInfo.setArchivedFile(zipFile);
                    //TODO #6: delete original file
                }
            } catch (Exception e) {
                fileInfo.setError(e);
            }
            result.addArchivedFile(fileInfo);
            reporter.report(1);
        }
        return result;
    }

    private File archiveFile(FileInfo fileInfo) throws Exception {
        ZipFile zipFile = new ZipFile(fileInfo.getOriginalFile().getAbsolutePath() + ".zip");

        ZipParameters params = new ZipParameters();
        params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);
        params.setEncryptFiles(true);
        params.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
        params.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        params.setPassword(this.options.getPassword());

        zipFile.addFile(fileInfo.getOriginalFile(), params);
        return zipFile.getFile();
    }

    static class FileStorage {
        private final List<File> files;

        public FileStorage(List<File> files) {
            this.files = files;
        }

        public synchronized File get() {
            if (this.files.isEmpty())
                return null;
            return this.files.remove(0);
        }
    }
}
