package com.citigroup.icg.cmc;

import java.util.ArrayList;

public class ValidationResultsPrinter implements Visitor<ValidationResults> {

    @Override
    public void visit(ValidationResults results) {
        /*
            TODO: System.out.println all validation error
            --input-path:
                * does not exist

            --password:
                * is not strong
                * is missing
        */
        ArrayList<String> intputPathErrors = results.getInputPathErrors();
        if (intputPathErrors != null) {
            //print error here
        }
    }
}
