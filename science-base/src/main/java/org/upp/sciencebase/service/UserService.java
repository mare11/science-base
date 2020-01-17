package org.upp.sciencebase.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.dto.LoginUserDto;
import org.upp.sciencebase.dto.UserDto;
import org.upp.sciencebase.exception.BadRequestException;
import org.upp.sciencebase.exception.UsernameExistsException;
import org.upp.sciencebase.model.User;
import org.upp.sciencebase.model.UserRoleEnum;
import org.upp.sciencebase.repository.UserRepository;

import java.util.Base64;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserTaskService userTaskService;
    private final TaskService taskService;

    @Autowired
    public UserService(UserRepository userRepository, UserTaskService userTaskService, TaskService taskService) {
        this.userRepository = userRepository;
        this.userTaskService = userTaskService;
        this.taskService = taskService;
    }

    public UserDto loginUser(LoginUserDto loginUserDto) {
        User user = userRepository.findByUsername(loginUserDto.getUsername());
        if (user == null || !user.isUserEnabled() || !user.getPassword().equals(loginUserDto.getPassword())) {
            throw new BadRequestException();
        }
        log.info("User: {} logged in!", user.getUsername());
        return UserDto.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public User getAdmin() {
        return userRepository.findByRole(UserRoleEnum.ADMIN);
    }

    public void validateUserData(User newUser) {
        log.info("Validating user data...");
        User user = userRepository.findByUsername(newUser.getUsername());
        if (user != null) {
            log.error("User data not valid!");
            throw new UsernameExistsException(newUser.getUsername());
        }
        log.info("User data valid!");
    }

    public void verifyUser(String code) {
        Task task = taskService.createTaskQuery()
                .processInstanceId(getProcessInstanceId(code))
                .active()
                .singleResult();
        if (task == null) {
            throw new BadRequestException();
        }
        userTaskService.completeTask(task.getId());
    }

    private String getProcessInstanceId(String code) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(code);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }
    }
}
