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
import org.upp.sciencebase.repository.MagazineRepository;
import org.upp.sciencebase.repository.ScienceAreaRepository;
import org.upp.sciencebase.repository.TextRepository;
import org.upp.sciencebase.util.MultiEnumFormType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.upp.sciencebase.util.ProcessUtil.*;

@Slf4j
@Service
public class EditorService {

    private final UserTaskService userTaskService;
    private final TaskService taskService;
    private final FormService formService;
    private final RuntimeService runtimeService;
    private final ScienceAreaRepository scienceAreaRepository;
    private final MagazineRepository magazineRepository;
    private final TextRepository textRepository;

    @Autowired
    public EditorService(UserTaskService userTaskService, TaskService taskService, FormService formService, RuntimeService runtimeService, ScienceAreaRepository scienceAreaRepository, MagazineRepository magazineRepository, TextRepository textRepository) {
        this.userTaskService = userTaskService;
        this.taskService = taskService;
        this.formService = formService;
        this.runtimeService = runtimeService;
        this.scienceAreaRepository = scienceAreaRepository;
        this.magazineRepository = magazineRepository;
        this.textRepository = textRepository;
    }

    public List<MagazineDto> getMagazines(String username) {
        return magazineRepository.findAllByMainEditor_Username(username).stream()
                .map(magazine ->
                        MagazineDto.builder()
                                .name(magazine.getName())
                                .issn(magazine.getIssn())
                                .paymentMethod(magazine.getPaymentMethod())
                                .enabled(magazine.isEnabled())
                                .taskDto(getCorrectionTaskDataForMagazine(username, magazine.getName()))
                                .build())
                .collect(Collectors.toList());
    }


    public TaskDto getCorrectionTaskData(String taskId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        Map<String, String> scienceAreas = scienceAreaRepository.findAll().stream()
                .collect(Collectors.toMap(ScienceArea::getAreaKey, ScienceArea::getAreaValue));
        List<FormFieldDto> formFieldDtos = new ArrayList<>();
        formService.getTaskFormData(task.getId()).getFormFields().forEach(formField -> {
            FormFieldDto fieldDto = new FormFieldDto(formField);
            if (SELECTED_AREAS_FIELD.equals(formField.getId())) {
                Map<String, String> values = new HashMap<>();
                scienceAreas.forEach(values::put);
                fieldDto.setType(new MultiEnumFormType(values));
            }
            formFieldDtos.add(fieldDto);
        });
        return TaskDto.builder()
                .taskId(task.getId())
                .taskName(task.getName())
                .formFields(formFieldDtos)
                .build();
    }

    public List<TextDto> getEditorTextsWithActiveTask(String username) {
        return textRepository.findAll().stream()
                .map(text ->
                        TextDto.builder()
                                .title(text.getTitle())
                                .keyTerms(text.getKeyTerms())
                                .apstract(text.getApstract())
                                .author(text.getAuthor().getFullName())
                                .taskDto(getActiveTaskDataForText(username, text.getTitle()))
                                .build())
                .filter(textDto -> textDto.getTaskDto() != null)
                .collect(Collectors.toList());
    }

    public TaskDto getMagazineTextForm(String taskId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        List<FormFieldDto> formFieldDtos = new ArrayList<>();
        formService.getTaskFormData(task.getId()).getFormFields().forEach(formField -> {
            FormFieldDto fieldDto = new FormFieldDto(formField);
            if (SELECT_REVIEWERS_TASK.equals(task.getTaskDefinitionKey())) {
                fieldDto.setType(new MultiEnumFormType(getReviewersForArea(task.getProcessInstanceId())));
            }
            formFieldDtos.add(fieldDto);
        });
        return TaskDto.builder()
                .taskId(task.getId())
                .taskName(task.getName())
                .formFields(formFieldDtos)
                .build();
    }

    private TaskDto getCorrectionTaskDataForMagazine(String username, String magazineName) {
        List<Task> userTasks = userTaskService.getActiveUserTasksForSpecificProcess(username, NEW_MAGAZINE_PROCESS_KEY);
        for (Task task : userTasks) {
            String name = runtimeService.getVariable(task.getProcessInstanceId(), "name").toString();
            if (magazineName.equals(name)) {
                return TaskDto.builder()
                        .taskId(task.getId())
                        .taskName(task.getName())
                        .build();
            }
        }
        return null;
    }

    private TaskDto getActiveTaskDataForText(String username, String textTitle) {
        List<Task> userTasks = userTaskService.getActiveUserTasksForSpecificProcess(username, NEW_TEXT_PROCESS_KEY);
        for (Task task : userTasks) {
            String title = String.valueOf(runtimeService.getVariable(task.getProcessInstanceId(), "title"));
            if (textTitle.equals(title)) {
                return TaskDto.builder()
                        .taskId(task.getId())
                        .taskName(task.getName())
                        .taskAssignee(task.getAssignee())
                        .build();
            }
        }
        return null;
    }

    private Map<String, String> getReviewersForArea(String processInstanceId) {
        Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
        List<UserDto> reviewersForArea = (List<UserDto>) variables.get("reviewersForArea");
        return reviewersForArea.stream()
                .filter(reviewer -> !reviewer.getUsername().equals(variables.get("processInitiator")))
                .collect(Collectors.toMap(UserDto::getUsername, UserDto::getFullName));
    }
}
