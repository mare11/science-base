package org.upp.sciencebase.registration;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.service.UserService;

@Slf4j
@Service
public class StartProcessHandler implements ExecutionListener {

    private final UserService userService;

    @Autowired
    public StartProcessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void notify(DelegateExecution execution) {
        log.info("Registration process started!");
    }
}
