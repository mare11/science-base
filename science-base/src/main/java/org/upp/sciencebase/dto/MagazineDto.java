package org.upp.sciencebase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MagazineDto {
    private String name;
    private String issn;
    private Set<String> scienceAreas;
    private String paymentMethod;
    private FormFieldsDto formFieldsDto;
}
