package com.citigroup.icg.cmc;

import org.apache.commons.cli.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ArchiveOptions {
    private Path inputPath;
    private String password;
    private Collection<String> skipExt;
    private Integer skipOlderThan;
    private boolean test;
    private Path outputFilePath;
    private boolean help;
    private static final Options helpOptions;
    private static final Options requiredOptions;
    private final ValidationResults validationResults = new ValidationResults();
    private final Pattern upperPattern = Pattern.compile("[A-Z]");
    private final Pattern lowerPattern = Pattern.compile("[a-z]");
    private final Pattern numbersPattern = Pattern.compile("[0-9]");
    private final Pattern alphaNumericPattern = Pattern.compile("[^a-zA-Z0-9]");

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
        this.setSkipOlderThan(cmd);
        this.setTest(cmd);
        this.setOutputFilePath(cmd);
        this.setHelp(cmd);
    }

    public ValidationResults getValidationResults() {
        return validationResults;
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

        if (inputPath == null)
            validationResults.addInputPathError("Input path not provided.");
        else if (Files.notExists(inputPath))
            validationResults.addInputPathError("Input path does not exist.");
    }

    public String getPassword() {
        return this.password;
    }

    private void setPassword(CommandLine cmd) {
        if (cmd.hasOption("password"))
            this.password = cmd.getOptionValue("password");

        if (password == null) {
            validationResults.addPasswordError("The password not provided.");
        } else {
            if (password.length() < 6)
                validationResults.addPasswordError("The password contains less than 6 chars.");
            if (!textMatches(upperPattern, password))
                validationResults.addPasswordError("The password does not contain upper case letters.");
            if (!textMatches(lowerPattern, password))
                validationResults.addPasswordError("The password does not contain lower case letters.");
            if (!textMatches(numbersPattern, password))
                validationResults.addPasswordError("The password does not contain numbers.");
            if (!textMatches(alphaNumericPattern, password))
                validationResults.addPasswordError("The password does not contain special characters.");
        }
    }

    public Collection<String> getSkipExt() {
        return this.skipExt;
    }

    private void setSkipExt(CommandLine cmd) {
        if (!cmd.hasOption("skip-ext"))
            return;
        this.skipExt = Arrays.stream(cmd.getOptionValue("skip-ext").split(",")) // 0,1
                .distinct()
                .map(x -> x.toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(ArrayList<String>::new));

        for (String ext : skipExt) {
            if (textMatches(alphaNumericPattern, ext)) {
                validationResults.addSkipExtError(String.format("'%s' contains special chars.", ext));
            }
        }
    }

    public Integer getSkipOlderThan() {
        return this.skipOlderThan;
    }

    private void setSkipOlderThan(CommandLine cmd) {
        if (!cmd.hasOption("skip-older-than"))
            return;
        String skipOlderThanValue = cmd.getOptionValue("skip-older-than");
        if (skipOlderThanValue == null)
            return;
        try {
            this.skipOlderThan = Integer.parseInt(skipOlderThanValue);
            if (this.skipOlderThan <= 0) {
                validationResults.addSkipOlderThanErrors("Must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            validationResults.addSkipOlderThanErrors("Invalid number.");
        }
        if (skipOlderThanValue.startsWith("0")) {
            validationResults.addSkipOlderThanErrors("Must start with a number greater than 0.");
        }
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
        else
            return;

        if (Files.exists(outputFilePath)) {
            try {
                Files.deleteIfExists(outputFilePath);
                Files.createFile(outputFilePath);
            } catch (Exception e) {
                validationResults.addOutputFilePathError("Output file path is invalid.");
            }
        }
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