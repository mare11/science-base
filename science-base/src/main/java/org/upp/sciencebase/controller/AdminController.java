package org.upp.sciencebase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upp.sciencebase.dto.FormSubmissionDto;
import org.upp.sciencebase.dto.MagazineDto;
import org.upp.sciencebase.dto.ReviewerDto;
import org.upp.sciencebase.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/reviewers")
    public ResponseEntity<List<ReviewerDto>> getNonEnabledReviewers() {
        return ResponseEntity.ok(adminService.getNonEnabledReviewers());
    }

    @PutMapping("/reviewers/{taskId}")
    public ResponseEntity<Void> enableReviewer(@PathVariable String taskId) {
        adminService.enableReviewer(taskId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/magazines")
    public ResponseEntity<List<MagazineDto>> getNonApprovedMagazines() {
        return ResponseEntity.ok(adminService.getNonApprovedMagazines());
    }

    @PutMapping("/magazines/{taskId}")
    public ResponseEntity<Void> approveMagazine(@RequestBody List<FormSubmissionDto> submittedFields, @PathVariable String taskId) {
        adminService.approveMagazine(submittedFields, taskId);
        return ResponseEntity.ok().build();
    }
}
