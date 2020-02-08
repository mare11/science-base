package org.upp.sciencebase.util;

import org.camunda.bpm.engine.impl.form.type.SimpleFormFieldType;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.Base64;

public class FileFormType extends SimpleFormFieldType {


    @Override
    public String getName() {
        return "file";
    }

    @Override
    public Object convertFormValueToModelValue(Object o) {
        return null;
    }

    @Override
    public String convertModelValueToFormValue(Object o) {
        return null;
    }

    @Override
    protected TypedValue convertValue(TypedValue typedValue) {
        if (typedValue.getValue() instanceof String) {
            String value = typedValue.getValue().toString();
            byte[] decodedBytes = Base64.getDecoder().decode(value.substring(value.indexOf(',') + 1));
            return Variables.byteArrayValue(decodedBytes, typedValue.isTransient());
        }
        return Variables.objectValue(typedValue.getValue(), typedValue.isTransient()).create();
    }
}
