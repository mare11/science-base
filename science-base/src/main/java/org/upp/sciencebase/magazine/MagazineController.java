package org.upp.sciencebase.magazine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upp.sciencebase.dto.FormFieldsDto;
import org.upp.sciencebase.dto.FormSubmissionDto;

import java.util.List;

@RestController
@RequestMapping("/magazine")
public class MagazineController {

    private final MagazineService magazineService;

    @Autowired
    public MagazineController(MagazineService magazineService) {
        this.magazineService = magazineService;
    }

    @GetMapping(value = "/{username}")
    public ResponseEntity<FormFieldsDto> startNewMagazineProcess(@PathVariable String username) {
        return ResponseEntity.ok(magazineService.startProcess(username));
    }

    @PostMapping(value = "/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FormFieldsDto> submitForm(@RequestBody List<FormSubmissionDto> submittedFields, @PathVariable String taskId) {
        return ResponseEntity.ok(magazineService.submitFormAndGetNextTaskFields(submittedFields, taskId));
    }
}
