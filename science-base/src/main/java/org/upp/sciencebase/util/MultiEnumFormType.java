package org.upp.sciencebase.util;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.TypedValue;

import java.util.Collection;
import java.util.Map;

public class MultiEnumFormType extends EnumFormType {

    public MultiEnumFormType(Map<String, String> values) {
        super(values);
    }

    @Override
    public String getName() {
        return "multi-enum";
    }

    @Override
    protected void validateValue(Object value) {
        Collection<?> collection = (Collection<?>) value;
        collection.forEach(o -> {
            if (this.values != null && !this.values.containsKey(o)) {
                throw new ProcessEngineException("Invalid value for enum form property: " + value);
            }
        });
    }

    @Override
    public TypedValue convertValue(TypedValue propertyValue) {
        Object value = propertyValue.getValue();
        if (value != null && !(value instanceof Collection)) {
            throw new ProcessEngineException("Value '" + value + "' is not of type Collection.");
        } else {
//            this.validateValue(value);
            return Variables.objectValue(value, propertyValue.isTransient()).create();
        }
    }
}
