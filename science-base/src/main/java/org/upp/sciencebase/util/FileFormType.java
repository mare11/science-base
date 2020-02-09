package org.upp.sciencebase.util;

import org.camunda.bpm.engine.impl.form.type.StringFormType;

public class FileFormType extends StringFormType {

    @Override
    public String getName() {
        return "file";
    }

}
