package org.upp.sciencebase.text;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.upp.sciencebase.dto.FormSubmissionDto;
import org.upp.sciencebase.dto.TaskDto;
import org.upp.sciencebase.dto.TextDto;

import java.util.List;

@RestController
@RequestMapping(value = "/text")
public class TextController {

    private final TextService textService;

    @Autowired
    public TextController(TextService textService) {
        this.textService = textService;
    }

    @GetMapping(value = "/{magazineName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDto> startNewTextProcess(@PathVariable String magazineName, @RequestParam String username) {
        return ResponseEntity.ok(textService.startProcess(magazineName, username));
    }

    @PostMapping(value = "/upload/{title}")
    public ResponseEntity<Void> uploadTextFile(@PathVariable String title, @RequestParam("file") MultipartFile file,
                                               @RequestParam("overwrite") boolean overwrite) {
        textService.saveTextFile(title, file, overwrite);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/download/{taskId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadTextFile(@PathVariable String taskId) {
        return ResponseEntity.ok(textService.getTextFile(taskId));
    }

    @PostMapping(value = "/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDto> submitForm(@RequestBody List<FormSubmissionDto> submittedFields, @PathVariable String taskId) {
        return ResponseEntity.ok(textService.submitFormAndGetNextTaskData(submittedFields, taskId));
    }

    @GetMapping("/all/{username}")
    public ResponseEntity<List<TextDto>> getUserTexts(@PathVariable String username) {
        return ResponseEntity.ok(textService.getUserTexts(username));
    }
}
