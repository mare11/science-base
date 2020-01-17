package org.upp.sciencebase.util;

import org.camunda.bpm.engine.impl.form.type.StringFormType;

public class EmailFormType extends StringFormType {

    @Override
    public String getName() {
        return "email";
    }
}
