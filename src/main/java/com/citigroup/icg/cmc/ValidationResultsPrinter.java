package com.citigroup.icg.cmc;

import java.util.Collection;

public class ValidationResultsPrinter implements Visitor<ValidationResults> {

    @Override
    public void visit(ValidationResults results) {
        /*
            TODO: System.out.println all validation error
            Validation errors:
            input-path
                - does not exist
            password
                - is not strong
                - is missing
        */
        Collection<String> intputPathErrors = results.getInputPathErrors();
        if (!intputPathErrors.isEmpty()) {
            //print error here
        }
    }
}
