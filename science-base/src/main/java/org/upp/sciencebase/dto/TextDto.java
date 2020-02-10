package org.upp.sciencebase.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextDto {
    private String title;
    private String keyTerms;
    private String apstract;
    private boolean accepted;
    private String author;
    private String magazineName;
    private String mainEditor;
    private TaskDto taskDto;
}
