package org.upp.sciencebase.registration;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.model.User;
import org.upp.sciencebase.repository.UserRepository;

@Slf4j
@Service
public class EnableReviewerService implements JavaDelegate {

    private final UserRepository userRepository;

    @Autowired
    public EnableReviewerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        User user = userRepository.findByUsername(execution.getVariable("username").toString());
        log.info("Enabling reviewer: {}", user.getUsername());
        user.setReviewerEnabled(true);
        userRepository.save(user);
    }
}
