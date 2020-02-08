package org.upp.sciencebase.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upp.sciencebase.dto.FormSubmissionDto;
import org.upp.sciencebase.dto.TaskDto;
import org.upp.sciencebase.service.UserService;
import org.upp.sciencebase.service.UserTaskService;

import java.util.List;

import static org.upp.sciencebase.util.ProcessUtil.REGISTRATION_PROCESS_KEY;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    private final UserService userService;
    private final UserTaskService userTaskService;

    @Autowired
    public RegistrationController(UserService userService, UserTaskService userTaskService) {
        this.userService = userService;
        this.userTaskService = userTaskService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDto> startUserRegistration() {
        return ResponseEntity.ok(userTaskService.startProcessAndGetFormFields(REGISTRATION_PROCESS_KEY, null));
    }

    @PostMapping(value = "/{taskId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> submitForm(@RequestBody List<FormSubmissionDto> submittedFields, @PathVariable String taskId) {
        userTaskService.submitForm(submittedFields, taskId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/verify/{code}")
    public ResponseEntity<Void> verifyUser(@PathVariable String code) {
        userService.verifyUser(code);
        return ResponseEntity.ok().build();
    }
}
