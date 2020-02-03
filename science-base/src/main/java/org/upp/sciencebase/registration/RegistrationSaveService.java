package org.upp.sciencebase.registration;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.model.ScienceArea;
import org.upp.sciencebase.model.User;
import org.upp.sciencebase.model.UserRoleEnum;
import org.upp.sciencebase.repository.ScienceAreaRepository;
import org.upp.sciencebase.repository.UserRepository;
import org.upp.sciencebase.service.UserService;
import org.upp.sciencebase.util.EmailService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RegistrationSaveService implements JavaDelegate {

    private final UserService userService;
    private final EmailService emailService;
    private final IdentityService identityService;
    private final UserRepository userRepository;
    private final ScienceAreaRepository scienceAreaRepository;

    @Autowired
    public RegistrationSaveService(UserService userService, EmailService emailService, IdentityService identityService, UserRepository userRepository, ScienceAreaRepository scienceAreaRepository) {
        this.userService = userService;
        this.emailService = emailService;
        this.identityService = identityService;
        this.userRepository = userRepository;
        this.scienceAreaRepository = scienceAreaRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        User user = extractUserVariables(execution);
        userService.validateUserData(user);

        org.camunda.bpm.engine.identity.User camundaUser = identityService.newUser(user.getUsername());
        camundaUser.setPassword(user.getPassword());
        camundaUser.setFirstName(user.getFirstName());
        camundaUser.setLastName(user.getLastName());
        camundaUser.setEmail(user.getEmail());

        log.info("Saving user: {}", user.getUsername());
        userRepository.save(user);
        identityService.saveUser(camundaUser);

        emailService.sendRegistrationMail(user, execution.getProcessInstanceId());
    }

    private User extractUserVariables(DelegateExecution execution) {
        log.info("Execution variables:");
        execution.getVariables().forEach((s, o) ->
                log.info("Variable: {}, value: {}", s, o)
        );
        User.UserBuilder userBuilder = User.builder()
                .username(execution.getVariable("username").toString())
                .password(execution.getVariable("password").toString())
                .email(execution.getVariable("email").toString())
                .firstName(execution.getVariable("firstName").toString())
                .lastName(execution.getVariable("lastName").toString())
                .country(execution.getVariable("country").toString())
                .city(execution.getVariable("city").toString())
                .title(execution.getVariable("title").toString())
                .role(UserRoleEnum.USER)
                .reviewer((Boolean) execution.getVariable("reviewer"))
                .userEnabled(false)
                .reviewerEnabled(false);

        List<String> areas = (List<String>) execution.getVariable("selectedAreas");
        Set<ScienceArea> areaSet = new HashSet<>();
        areas.forEach(area ->
                areaSet.add(scienceAreaRepository.findByAreaKey(area))
        );
        userBuilder.scienceAreas(areaSet);

        return userBuilder.build();
    }
}
