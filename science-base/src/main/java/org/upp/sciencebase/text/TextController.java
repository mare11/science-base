package org.upp.sciencebase.text;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upp.sciencebase.dto.FormFieldsDto;
import org.upp.sciencebase.dto.FormSubmissionDto;
import org.upp.sciencebase.service.UserTaskService;

import java.util.List;

@RestController
@RequestMapping(value = "/text")
public class TextController {

    private final TextService textService;
    private final UserTaskService userTaskService;

    @Autowired
    public TextController(TextService textService, UserTaskService userTaskService) {
        this.textService = textService;
        this.userTaskService = userTaskService;
    }

    @GetMapping(value = "/{magazineName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FormFieldsDto> startNewTextProcess(@PathVariable String magazineName, @RequestParam String username) {
        return ResponseEntity.ok(textService.startProcess(magazineName, username));
    }

    @GetMapping(value = "/download/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getTextFile(@PathVariable String fileName) {
        return ResponseEntity.ok(textService.getTextFile(fileName));
    }

    @PostMapping(value = "/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> submitForm(@RequestBody List<FormSubmissionDto> submittedFields, @PathVariable String taskId) {
        userTaskService.submitForm(submittedFields, taskId);
        return ResponseEntity.ok().build();
    }
}
