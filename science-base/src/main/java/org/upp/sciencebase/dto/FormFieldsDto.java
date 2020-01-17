package org.upp.sciencebase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormFieldsDto {
    String processInstanceId;
    String taskId;
    List<FormFieldDto> formFields;
}
