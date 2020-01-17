package org.upp.sciencebase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upp.sciencebase.dto.LoginUserDto;
import org.upp.sciencebase.dto.TaskDto;
import org.upp.sciencebase.dto.UserDto;
import org.upp.sciencebase.service.UserService;
import org.upp.sciencebase.service.UserTaskService;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserTaskService userTaskService;
    private final UserService userService;

    @Autowired
    public UserController(UserTaskService userTaskService, UserService userService) {
        this.userTaskService = userTaskService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@RequestBody LoginUserDto loginUserDto) {
        return ResponseEntity.ok(userService.loginUser(loginUserDto));
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<TaskDto>> getUserTasks(@PathVariable String username) {
        return ResponseEntity.ok(userTaskService.getUserTasks(username));
    }
}
