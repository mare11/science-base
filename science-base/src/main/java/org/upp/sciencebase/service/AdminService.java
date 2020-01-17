package org.upp.sciencebase.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.dto.*;
import org.upp.sciencebase.model.ScienceArea;
import org.upp.sciencebase.repository.PaymentMethodRepository;
import org.upp.sciencebase.repository.ScienceAreaRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.upp.sciencebase.util.ProcessUtil.*;

@Slf4j
@Service
public class AdminService {

    private final TaskService taskService;
    private final UserTaskService userTaskService;
    private final ScienceAreaRepository scienceAreaRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final RuntimeService runtimeService;
    private final FormService formService;

    @Autowired
    public AdminService(RuntimeService runtimeService, TaskService taskService, UserTaskService userTaskService, ScienceAreaRepository scienceAreaRepository, PaymentMethodRepository paymentMethodRepository, FormService formService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.userTaskService = userTaskService;
        this.scienceAreaRepository = scienceAreaRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.formService = formService;
    }

    public List<ReviewerDto> getNonEnabledReviewers() {
        List<Task> taskList = getAdminTasksForProcessKey(REGISTRATION_PROCESS_KEY);

        return taskList.stream()
                .map(task -> {
                    Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());
                    return ReviewerDto.builder()
                            .username(variables.get("username").toString())
                            .firstName(variables.get("firstName").toString())
                            .lastName(variables.get("lastName").toString())
                            .email(variables.get("email").toString())
                            .taskId(task.getId())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void enableReviewer(String taskId) {
        userTaskService.completeTask(taskId);
    }

    public List<MagazineDto> getNonApprovedMagazines() {
        List<Task> taskList = getAdminTasksForProcessKey(NEW_MAGAZINE_PROCESS_KEY);

        return taskList.stream()
                .map(task -> {
                    Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());
                    MagazineDto.MagazineDtoBuilder dtoBuilder = MagazineDto.builder()
                            .name(variables.get("name").toString())
                            .issn(variables.get("issn").toString())
                            .paymentMethod(paymentMethodRepository.findByMethodKey(variables.get("paymentMethod").toString()).getMethodValue())
                            .formFieldsDto(FormFieldsDto.builder()
                                    .processInstanceId(task.getProcessInstanceId())
                                    .taskId(task.getId())
                                    .formFields(formService.getTaskFormData(task.getId()).getFormFields()
                                            .stream()
                                            .map(FormFieldDto::new)
                                            .collect(Collectors.toList()))
                                    .build());
                    List<String> selectedAreas = (List<String>) variables.get("selectedAreas");
                    Set<String> areas = new HashSet<>();
                    selectedAreas.forEach(area -> {
                        ScienceArea scienceArea = scienceAreaRepository.findByAreaKey(area);
                        areas.add(scienceArea.getAreaValue());
                    });
                    dtoBuilder.scienceAreas(areas);
                    return dtoBuilder.build();
                })
                .collect(Collectors.toList());
    }

    public void approveMagazine(List<FormSubmissionDto> submittedFields, String taskId) {
        userTaskService.submitForm(submittedFields, taskId);
    }

    private List<Task> getAdminTasksForProcessKey(String processKey) {
        return taskService.createTaskQuery()
                .processDefinitionKey(processKey)
                .taskAssignee(ADMIN_USERNAME)
                .active()
                .list();
    }
}
