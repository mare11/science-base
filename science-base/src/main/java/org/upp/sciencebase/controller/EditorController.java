package org.upp.sciencebase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upp.sciencebase.dto.FormFieldsDto;
import org.upp.sciencebase.dto.TaskDto;
import org.upp.sciencebase.service.EditorService;

import java.util.List;

@RestController
@RequestMapping("/editors")
public class EditorController {

    private final EditorService editorService;

    @Autowired
    public EditorController(EditorService editorService) {
        this.editorService = editorService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<TaskDto>> getMagazinesForCorrection(@PathVariable String username) {
        return ResponseEntity.ok(editorService.getMagazinesForCorrection(username));
    }

    @GetMapping("/form/{taskId}")
    public ResponseEntity<FormFieldsDto> getCorrectionTaskForm(@PathVariable String taskId) {
        return ResponseEntity.ok(editorService.getCorrectionTaskFormFields(taskId));
    }

}
