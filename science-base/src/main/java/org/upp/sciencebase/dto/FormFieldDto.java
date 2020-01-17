package org.upp.sciencebase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.FormFieldValidationConstraint;
import org.camunda.bpm.engine.form.FormType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormFieldDto {
    private String id;
    private String label;
    private FormType type;
    private Object value;
    private List<FormFieldValidationConstraint> validationConstraints;

    public FormFieldDto(FormField formField) {
        this.id = formField.getId();
        this.label = formField.getLabel();
        this.type = formField.getType();
        this.value = formField.getValue().getValue();
        this.validationConstraints = formField.getValidationConstraints();
    }
}
