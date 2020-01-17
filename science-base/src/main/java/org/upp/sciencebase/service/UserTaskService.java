package org.upp.sciencebase.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.dto.FormFieldDto;
import org.upp.sciencebase.dto.FormFieldsDto;
import org.upp.sciencebase.dto.FormSubmissionDto;
import org.upp.sciencebase.dto.TaskDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.upp.sciencebase.util.ProcessUtil.PROCESS_INITIATOR_FIELD;

@Slf4j
@Service
public class UserTaskService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final FormService formService;

    @Autowired
    public UserTaskService(RuntimeService runtimeService, TaskService taskService, FormService formService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.formService = formService;
    }

    public FormFieldsDto startProcessAndGetFormFields(String processKey, String username) {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey);
        runtimeService.setVariable(processInstance.getId(), PROCESS_INITIATOR_FIELD, username);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list().get(0);
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        return FormFieldsDto.builder()
                .processInstanceId(processInstance.getId())
                .taskId(task.getId())
                .formFields(taskFormData.getFormFields()
                        .stream()
                        .map(FormFieldDto::new)
                        .collect(Collectors.toList()))
                .build();
    }

    public void submitForm(List<FormSubmissionDto> submittedFields, String taskId) {
        Map<String, Object> fieldsMap = submittedFields.stream()
                .collect(HashMap::new, (map, field) -> map.put(field.getFieldId(), field.getFieldValue()), HashMap::putAll);
        formService.submitTaskForm(taskId, fieldsMap);
        log.info("Form for task with id: {} submitted with fields: {}", taskId, submittedFields);
    }

    public void claimTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String user = runtimeService.getVariable(task.getProcessInstanceId(), "username").toString();
        taskService.claim(taskId, user);
        log.info("Task with id: {} claimed by user: {}", taskId, user);
    }

    public void completeTask(String taskId) {
        taskService.complete(taskId);
        log.info("Task with id: {} completed", taskId);
    }

    public List<TaskDto> getUserTasks(String username) {
        log.info("Fetching user tasks for username: {}", username);
        return taskService.createTaskQuery()
                .taskAssignee(username)
                .orderByDueDate()
                .asc()
                .list()
                .stream()
                .map(task -> TaskDto.builder()
                        .taskId(task.getId())
                        .name(task.getName())
                        .assignee(task.getAssignee())
                        .build())
                .collect(Collectors.toList());
    }
}
