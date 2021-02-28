package com.citigroup.icg.cmc;

import java.util.ArrayList;

public class ValidationResults {
    private ArrayList<String> inputPathErrors;
    private ArrayList<String> passwordErrors;
    private ArrayList<String> skipExtErrors;
    private ArrayList<String> outputFilePathErrors;

    public ArrayList<String> getInputPathErrors() {
        return inputPathErrors;
    }

    public void addInputPathError(String Error) {
        this.inputPathErrors.add(Error);
    }

    public ArrayList<String> getPasswordErrors() {
        return passwordErrors;
    }

    public void addPasswordError(String Error) {
        this.passwordErrors.add(Error);
    }

    public ArrayList<String> getSkipExtErrors() {
        return skipExtErrors;
    }

    public void addSkipExtError(String Error) {
        this.skipExtErrors.add(Error);
    }

    public ArrayList<String> getOutputFilePathErrors() {
        return outputFilePathErrors;
    }

    public void addOutputFilePathError(String Error) {
        this.outputFilePathErrors.add(Error);
    }

    public boolean isValid() {
        return inputPathErrors == null && passwordErrors == null && skipExtErrors == null
                && outputFilePathErrors == null;
    }

    public void accept(Visitor<ValidationResults> visitor) {
        visitor.visit(this);
    }
}
