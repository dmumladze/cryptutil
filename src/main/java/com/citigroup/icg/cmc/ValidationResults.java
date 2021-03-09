package com.citigroup.icg.cmc;

import java.util.ArrayList;
import java.util.Collection;

public class ValidationResults {
    private final ArrayList<String> inputPathErrors = new ArrayList<>();
    private final ArrayList<String> passwordErrors = new ArrayList<>();
    private final ArrayList<String> skipExtErrors = new ArrayList<>();
    private final ArrayList<String> skipOlderThanErrors = new ArrayList<>();
    private final ArrayList<String> outputFilePathErrors = new ArrayList<>();

    public Collection<String> getInputPathErrors() {
        return inputPathErrors;
    }

    public void addInputPathError(String error) {
        inputPathErrors.add(error);
    }

    public Collection<String> getPasswordErrors() {
        return passwordErrors;
    }

    public void addPasswordError(String error) {
        passwordErrors.add(error);
    }

    public Collection<String> getSkipExtErrors() {
        return skipExtErrors;
    }

    public void addSkipExtError(String error) {
        skipExtErrors.add(error);
    }

    public Collection<String> getskipOlderThanErrors() {
        return skipOlderThanErrors;
    }

    public void addSkipOlderThanErrors(String error) {
        skipOlderThanErrors.add(error);
    }

    public Collection<String> getOutputFilePathErrors() {
        return outputFilePathErrors;
    }

    public void addOutputFilePathError(String error) {
        outputFilePathErrors.add(error);
    }

    public boolean isInvalid() {
        return !inputPathErrors.isEmpty() || !passwordErrors.isEmpty() || !skipExtErrors.isEmpty()
                || !outputFilePathErrors.isEmpty() || !skipOlderThanErrors.isEmpty();
    }

    public void accept(Visitor<ValidationResults> visitor) {
        visitor.visit(this);
    }
}
