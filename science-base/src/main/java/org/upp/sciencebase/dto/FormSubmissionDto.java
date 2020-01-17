package org.upp.sciencebase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormSubmissionDto implements Serializable {
    private static final long serialVersionUID = -8214764633705912023L;

    private String fieldId;
    private Object fieldValue;
}
