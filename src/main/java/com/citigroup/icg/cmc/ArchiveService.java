package com.citigroup.icg.cmc;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ArchiveService {
    private FileStorage storage;
    private final ArchiveOptions options;

    public ArchiveService(ArchiveOptions options) {
        this.options = options;
    }

    public ArchiveResults runArchiver(List<File> files, ProgressReporter reporter) {
        this.storage = new FileStorage(files);

        reporter.log("Found %d files %n", files.size() - 1);
        reporter.init(files.size() - 1);

        List<Callable<ArchiveResults>> tasks = new ArrayList<>();
        //use only 70% of processors' cores
        int processorCount = (int) Math.ceil((Runtime.getRuntime().availableProcessors() / 100.0) * 70);

        for (int i = 0; i <= processorCount; i++) {
            tasks.add(() -> archiverTask(reporter));
        }

        ExecutorService exec = Executors.newFixedThreadPool(processorCount);
        ArchiveResults results = new ArchiveResults();
        try {
            List<Future<ArchiveResults>> futureResults = exec.invokeAll(tasks);
            for (Future<ArchiveResults> result : futureResults) {
                for (ArchiveFileInfo fileInfo : result.get().getArchivedFiles()) {
                    results.addArchivedFile(fileInfo);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            reporter.log(e.getMessage());
        } finally {
            exec.shutdown();
        }
        return results;
    }

    private ArchiveResults archiverTask(ProgressReporter reporter) {
        ArchiveResults result = new ArchiveResults();
        while (true) {
            File file = this.storage.get();
            if (file == null) {
                break;
            }
            ArchiveFileInfo fileInfo = new ArchiveFileInfo(file.getAbsolutePath());
            try {
                File zipFile = archiveFile(file);
                if (this.options.isTest()) {
                    //delete file if program running with --test option
                    Files.deleteIfExists(Paths.get(zipFile.getAbsolutePath()));
                } else {
                    fileInfo.setArchivedFile(zipFile.getAbsolutePath());
                    if (file.setWritable(true))
                        Files.delete(file.toPath());
                }
            } catch (Exception e) {
                fileInfo.setException(e);
            }
            result.addArchivedFile(fileInfo);
            reporter.report(1);
        }
        return result;
    }

    private File archiveFile(File file) throws Exception {
        ZipFile zipFile = new ZipFile(file.getAbsolutePath() + ".zip");

        ZipParameters params = new ZipParameters();
        params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);
        params.setEncryptFiles(true);
        params.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
        params.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
        params.setPassword(this.options.getPassword());

        zipFile.addFile(file, params);
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
