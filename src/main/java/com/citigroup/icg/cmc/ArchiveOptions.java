package com.citigroup.icg.cmc;

import org.apache.commons.cli.*;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ArchiveOptions {
    private Path inputPath;
    private String password;
    private Collection<String> skipExt;
    private boolean test;
    private Path outputFilePath;
    private boolean help;
    private static final Options helpOptions;
    private static final Options requiredOptions;

    static {
        helpOptions = new Options();
        helpOptions.addOption(null, "help", false, "Displays help for command-line options.");

        requiredOptions = new Options();
        requiredOptions.addRequiredOption("I", "input-path", true, "Directory or list of file to encrypt. Invalid files will be skipped without warning.");
        requiredOptions.addRequiredOption("P", "password", true, "Password for encrypted files.");
        requiredOptions.addOption("S", "skip-ext", true, "Skips files with provided extensions.");
        requiredOptions.addOption("D", "skip-older-than", true, "Skips files older than X days.");
        requiredOptions.addOption("T", "test", false, "Test run will neither encrypt nor deletes files, but will generate the full report.");
        requiredOptions.addOption("O", "output-file", true, "File to print the report to. If the file exists, it will be recreated with each run.");
        requiredOptions.addOption("H", "help", false, "Displays help for command-line options.");
    }

    public static ArchiveOptions parseHelp(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(helpOptions, args, true);
        return new ArchiveOptions(cmd);
    }

    public static ArchiveOptions parseRequired(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(requiredOptions, args);
        return new ArchiveOptions(cmd);
    }

    private ArchiveOptions(CommandLine cmd) {
        this.setInputPath(cmd);
        this.setPassword(cmd);
        this.setSkipExt(cmd);
        this.setTest(cmd);
        this.setOutputFilePath(cmd);
        this.setHelp(cmd);
    }

    public ValidationResults validate() {
        ValidationResults results = new ValidationResults();

        Pattern upper = Pattern.compile("[A-Z]");
        Pattern lower = Pattern.compile("[a-z]");
        Pattern numbers = Pattern.compile("[0-9]");
        Pattern alphaNumeric = Pattern.compile("[^a-zA-Z0-9]");

        if (Files.notExists(inputPath))
            results.addInputPathError("Input path does not exist.");

        if (password.length() < 6)
            results.addPasswordError("The password contains less than 6 chars.");
        if (!textMatches(upper, password))
            results.addPasswordError("The password does not contain upper case letters.");
        if (!textMatches(lower, password))
            results.addPasswordError("The password does not contain lower case letters.");
        if (!textMatches(numbers, password))
            results.addPasswordError("The password does not contain numbers.");
        if (!textMatches(alphaNumeric, password))
            results.addPasswordError("The password does not contain special characters.");

        for (String ext : skipExt) {
            if (textMatches(alphaNumeric, ext)) {
                results.addSkipExtError(String.format("'%s' contains special chars.", ext));
            }
        }

        if (outputFilePath != null) {
            try {
                Files.createFile(outputFilePath);
            } catch (Exception e) {
                results.addOutputFilePathError("Output file path is invalid.");
            }
        }

        //TODO: --skip-older-than argument should not be less then 0

        return results;
    }

    private boolean textMatches(Pattern pattern, String text) {
        Matcher m = pattern.matcher(text);
        return m.find();
    }

    public Path getInputPath() {
        return inputPath;
    }

    private void setInputPath(CommandLine cmd) {
        if (cmd.hasOption("input-path"))
            this.inputPath = Paths.get(cmd.getOptionValue("input-path"));
    }

    public String getPassword() {
        return this.password;
    }

    private void setPassword(CommandLine cmd) {
        if (cmd.hasOption("password"))
            this.password = cmd.getOptionValue("password");
    }

    public Collection<String> getSkipExt() {
        return this.skipExt;
    }

    private void setSkipExt(CommandLine cmd) {
        if (!cmd.hasOption("skip-ext"))
            return;
        this.skipExt = Arrays.stream(cmd.getOptionValue("skip-ext").split(","))
                .distinct()
                .map(x -> x.toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(ArrayList<String>::new));
    }

    public boolean isTest() {
        return this.test;
    }

    private void setTest(CommandLine cmd) {
        this.test = cmd.hasOption("test");
    }

    public Path getOutputFilePath() {
        return outputFilePath;
    }

    private void setOutputFilePath(CommandLine cmd) {
        if (cmd.hasOption("output-file"))
            this.outputFilePath = Paths.get(cmd.getOptionValue("output-file"));
    }

    public boolean isHelp() {
        return this.help;
    }

    private void setHelp(CommandLine cmd) {
        this.help = cmd.hasOption("help") || cmd.getArgList().isEmpty();
    }

    public void displayHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        formatter.printHelp("cryptutil", requiredOptions);
    }
}