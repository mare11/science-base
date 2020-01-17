package org.upp.sciencebase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upp.sciencebase.service.UserTaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final UserTaskService userTaskService;

    @Autowired
    public TaskController(UserTaskService userTaskService) {
        this.userTaskService = userTaskService;
    }

    @PutMapping(path = "/claim/{taskId}")
    public ResponseEntity<Void> claim(@PathVariable String taskId) {
        userTaskService.claimTask(taskId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/complete/{taskId}")
    public ResponseEntity<Void> complete(@PathVariable String taskId) {
        userTaskService.completeTask(taskId);
        return ResponseEntity.ok().build();
    }

}
