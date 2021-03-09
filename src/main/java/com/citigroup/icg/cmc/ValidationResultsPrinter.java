package com.citigroup.icg.cmc;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;

import java.util.Collection;


public class ValidationResultsPrinter implements Visitor<ValidationResults> {

    @Override
    public void visit(ValidationResults results) {
        Collection<String> inputPathErrors = results.getInputPathErrors();
        if (!inputPathErrors.isEmpty()) {
            System.out.println("--input-path");
            //inputPathErrors.forEach(System.out::println);
            inputPathErrors.forEach(e -> System.out.println("\t" + e));
        }

        Collection<String> passwordError = results.getPasswordErrors();
        if (!passwordError.isEmpty()) {
            System.out.println("--password");
            passwordError.forEach(e -> System.out.println("\t" + e));
        }

        Collection<String> skipExt = results.getSkipExtErrors();
        if (!skipExt.isEmpty()) {
            System.out.println("--skip-ext");
            skipExt.forEach(e -> System.out.println("\t" + e));
        }

        Collection<String> outputFilePathErrors = results.getOutputFilePathErrors();
        if (!outputFilePathErrors.isEmpty()) {
            System.out.println("--output-file");
            outputFilePathErrors.forEach(e -> System.out.println("\t" + e));
        }

        Collection<String> skipOlderThanDaysErrors = results.getskipOlderThanErrors();
        if (!skipOlderThanDaysErrors.isEmpty()) {
            System.out.println("--older-than-days");
            skipOlderThanDaysErrors.forEach(e -> System.out.println("\t" + e));
        }
    }
}
