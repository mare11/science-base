package org.upp.sciencebase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewerDto {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String taskId;
}
