package org.upp.sciencebase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upp.sciencebase.dto.MagazineDto;
import org.upp.sciencebase.dto.TaskDto;
import org.upp.sciencebase.dto.TextDto;
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

    @GetMapping("/magazines/{username}")
    public ResponseEntity<List<MagazineDto>> getMagazines(@PathVariable String username) {
        return ResponseEntity.ok(editorService.getMagazines(username));
    }

    @GetMapping("/magazines/form/{taskId}")
    public ResponseEntity<TaskDto> getCorrectionTaskForm(@PathVariable String taskId) {
        return ResponseEntity.ok(editorService.getCorrectionTaskData(taskId));
    }

    @GetMapping("/texts/{username}")
    public ResponseEntity<List<TextDto>> getMagazineTexts(@PathVariable String username) {
        return ResponseEntity.ok(editorService.getMagazineTexts(username));
    }

    @GetMapping("/texts/form/{taskId}")
    public ResponseEntity<TaskDto> getMagazineTextForm(@PathVariable String taskId) {
        return ResponseEntity.ok(editorService.getMagazineTextForm(taskId));
    }

//    @PutMapping("/texts/{taskId}")
//    public ResponseEntity<Void> approveText(@RequestBody List<FormSubmissionDto> submittedFields, @PathVariable String taskId) {
//        adminService.approveMagazine(submittedFields, taskId);
//        return ResponseEntity.ok().build();
//    }

}
