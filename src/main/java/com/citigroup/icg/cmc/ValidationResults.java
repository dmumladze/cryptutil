package com.citigroup.icg.cmc;

import java.util.ArrayList;

public class ValidationResults {
    private final ArrayList<String> inputPathErrors = new ArrayList<>();
    private final ArrayList<String> passwordErrors = new ArrayList<>();
    private final ArrayList<String> skipExtErrors = new ArrayList<>();
    private final ArrayList<String> outputFilePathErrors = new ArrayList<>();

    public ArrayList<String> getInputPathErrors() {
        return inputPathErrors;
    }

    public void addInputPathError(String error) {
        inputPathErrors.add(error);
    }

    public ArrayList<String> getPasswordErrors() {
        return passwordErrors;
    }

    public void addPasswordError(String error) {
        passwordErrors.add(error);
    }

    public ArrayList<String> getSkipExtErrors() {
        return skipExtErrors;
    }

    public void addSkipExtError(String error) {
        skipExtErrors.add(error);
    }

    public ArrayList<String> getOutputFilePathErrors() {
        return outputFilePathErrors;
    }

    public void addOutputFilePathError(String error) {
        outputFilePathErrors.add(error);
    }

    public boolean isValid() {
        return inputPathErrors.isEmpty() && passwordErrors.isEmpty() && skipExtErrors.isEmpty()
                && outputFilePathErrors.isEmpty();
    }

    public void accept(Visitor<ValidationResults> visitor) {
        visitor.visit(this);
    }
}