package com.citigroup.icg.cmc;

import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public class ArchiveOptions {
    private String path;
    private String password;
    private boolean recurse;
    private Collection<String> excludes;
    private boolean measure;
    private boolean help;
    private static final Options helpOptions;
    private static final Options requiredOptions;

    static {
        helpOptions = new Options();
        helpOptions.addOption("help", false, "Displays command-line options.");

        requiredOptions = new Options();
        requiredOptions.addRequiredOption("path", "", true, "Path to be searched for.");
        requiredOptions.addRequiredOption("password", "", true, "Password for encrypted files.");
        //TODO #1: add -input-file option
        //TODO #2: remove -recurse option
        requiredOptions.addOption("recurse", false, "Searches given path recursively.");
        //TODO #3: rename to "skip-ext"
        requiredOptions.addOption("exclude", true, "Pattern of the end path that is to be excluded.");
        requiredOptions.addOption("measure", false, "Measures the time it may take to complete.");
        requiredOptions.addOption("help", false, "Displays command-line options.");
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
        this.setPath(cmd);
        this.setPassword(cmd);
        this.setRecurse(cmd);
        this.setExcludes(cmd);
        this.setMeasure(cmd);
        this.setHelp(cmd);
    }
    public String getPath() {
        return this.path;
    }

    private void setPath(CommandLine cmd) {
        if (cmd.hasOption("path"))
            this.path = cmd.getOptionValue("path");
    }

    public String getPassword() {
        return this.password;
    }

    private void setPassword(CommandLine cmd) {
        if (cmd.hasOption("password"))
            this.password = cmd.getOptionValue("password");
    }

    public boolean isRecurse() {
        return this.recurse;
    }

    private void setRecurse(CommandLine cmd) {
        if (cmd.hasOption("password"))
            this.recurse = Boolean.parseBoolean(cmd.getOptionValue("password"));
    }

    public Collection<String> getExcludes() {
        return this.excludes;
    }

    private void setExcludes(CommandLine cmd) {
        if (!cmd.hasOption("exclude"))
            return;
        this.excludes = Arrays.stream(cmd.getOptionValue("exclude").split(","))
                .distinct()
                .map(x -> x.toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(ArrayList<String>::new));
    }

    //TODO #4: rename to test - isTest()
    public boolean isMeasure() {
        return this.measure;
    }

    private void setMeasure(CommandLine cmd) {
        this.measure = cmd.hasOption("measure");
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