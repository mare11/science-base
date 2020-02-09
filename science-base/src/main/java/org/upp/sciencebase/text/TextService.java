package org.upp.sciencebase.text;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.dto.*;
import org.upp.sciencebase.exception.BadRequestException;
import org.upp.sciencebase.model.Magazine;
import org.upp.sciencebase.model.ScienceArea;
import org.upp.sciencebase.model.Text;
import org.upp.sciencebase.repository.MagazineRepository;
import org.upp.sciencebase.repository.TextRepository;
import org.upp.sciencebase.service.UserTaskService;
import org.upp.sciencebase.util.MultiEnumFormType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.upp.sciencebase.util.ProcessUtil.*;

@Slf4j
@Service
public class TextService {

    private final TaskService taskService;
    private final FormService formService;
    private final RuntimeService runtimeService;
    private final UserTaskService userTaskService;
    private final TextRepository textRepository;
    private final MagazineRepository magazineRepository;

    @Autowired
    public TextService(TaskService taskService, FormService formService, RuntimeService runtimeService, UserTaskService userTaskService, TextRepository textRepository, MagazineRepository magazineRepository) {
        this.taskService = taskService;
        this.formService = formService;
        this.runtimeService = runtimeService;
        this.userTaskService = userTaskService;
        this.textRepository = textRepository;
        this.magazineRepository = magazineRepository;
    }

    public TaskDto startProcess(String magazineName, String username) {
        TaskDto taskDto = userTaskService.startProcessAndGetFormFields(NEW_TEXT_PROCESS_KEY, username);
        Task previousTask = taskService.createTaskQuery()
                .taskId(taskDto.getTaskId())
                .singleResult();

        FormFieldDto fieldDto = taskDto.getFormFields().get(0);
        FormSubmissionDto submissionDto = FormSubmissionDto.builder()
                .fieldId(fieldDto.getId())
                .fieldValue(magazineName)
                .build();
        userTaskService.submitForm(Collections.singletonList(submissionDto), taskDto.getTaskId());

        Task newTask = taskService.createTaskQuery()
                .processInstanceId(previousTask.getProcessInstanceId())
                .active()
                .singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(newTask.getId());
        Magazine magazine = magazineRepository.findByName(magazineName);
        if (ObjectUtils.isEmpty(magazine)) {
            throw new BadRequestException();
        }
        Map<String, String> scienceAreas = magazine.getScienceAreas().stream()
                .collect(Collectors.toMap(ScienceArea::getAreaKey, ScienceArea::getAreaValue));

        taskFormData.getFormFields().forEach(formField -> {
            if (SCIENCE_AREA_FIELD.equals(formField.getId())) {
                Map<String, String> values = ((EnumFormType) formField.getType()).getValues();
                scienceAreas.forEach(values::put);
            }
        });
        return TaskDto.builder()
                .taskId(newTask.getId())
                .taskName(newTask.getName())
                .formFields(taskFormData.getFormFields()
                        .stream()
                        .map(FormFieldDto::new)
                        .collect(Collectors.toList()))
                .build();
    }

    public Resource getTextFile(String title) {
        log.info("Reading file for text: {}", title);
        Text text = textRepository.findByTitle(title);
        if (ObjectUtils.isEmpty(text) || ObjectUtils.isEmpty(text.getFile())) {
            throw new BadRequestException();
        }
        return new ByteArrayResource(text.getFile());
    }

//    public void submitTextFile(MultipartFile file) {
//        String fileName = file.getOriginalFilename();
//        try {
//            Text text = Text.builder()
//                    .fileName(fileName)
//                    .content(file.getBytes())
//                    .build();
//            textRepository.save(text);
//        } catch (IOException e) {
//            log.info("Error while reading file: {}", fileName);
//        }
//        log.info("Submitted text file: {}", fileName);
//    }

    public TaskDto submitFormAndGetNextTaskData(List<FormSubmissionDto> submittedFields, String taskId) {
        Task previousTask = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        userTaskService.submitForm(submittedFields, previousTask.getId());
        List<Task> newTasks = taskService.createTaskQuery()
                .processInstanceId(previousTask.getProcessInstanceId())
                .active()
                .list();

        if (shouldNotReturnNextTaskData(newTasks)) {
            return null;
        }

        Task newTask = newTasks.get(0);
        if (newTask.getAssignee().equals(previousTask.getAssignee())) {
            List<FormFieldDto> formFields = new ArrayList<>();
            TaskFormData taskFormData = formService.getTaskFormData(newTask.getId());
            taskFormData.getFormFields().forEach(formField -> {
                FormFieldDto fieldDto = new FormFieldDto(formField);
                if (SELECT_REVIEWERS_TASK.equals(newTask.getTaskDefinitionKey())) {
                    fieldDto.setType(new MultiEnumFormType(getReviewersForArea(newTask.getProcessInstanceId())));
                }
                formFields.add(fieldDto);
            });
            return TaskDto.builder()
                    .taskId(newTask.getId())
                    .taskName(newTask.getName())
                    .formFields(formFields)
                    .build();
        }

        return null;
    }

    public List<TextDto> getUserTexts(String username) {
        return textRepository.findAll().stream()
                .map(text ->
                        TextDto.builder()
                                .title(text.getTitle())
                                .keyTerms(text.getKeyTerms())
                                .apstract(text.getApstract())
                                .magazineName(text.getMagazine().getName())
                                .mainEditor(text.getMagazine().getMainEditor().getUsername())
                                .taskDto(getActiveTaskDataForText(username, text.getTitle()))
                                .build())
                .collect(Collectors.toList());
    }

    private boolean shouldNotReturnNextTaskData(List<Task> newTasks) {
        return ObjectUtils.isEmpty(newTasks) ||
                (newTasks.size() > 1 && !newTasks.get(0).getTaskDefinitionKey().equals(READ_REVIEW_AUTHOR_TASK));
    }

    private Map<String, String> getReviewersForArea(String processInstanceId) {
        Map<String, Object> variables = runtimeService.getVariables(processInstanceId);
        List<UserDto> reviewersForArea = (List<UserDto>) variables.get("reviewersForArea");
        return reviewersForArea.stream()
                .filter(reviewer -> !reviewer.getUsername().equals(variables.get("processInitiator")))
                .collect(Collectors.toMap(UserDto::getUsername, UserDto::getFullName));
    }

    private TaskDto getActiveTaskDataForText(String username, String textTitle) {
        List<Task> userTasks = userTaskService.getActiveUserTasksForSpecificProcess(username, NEW_TEXT_PROCESS_KEY);
        for (Task task : userTasks) {
            String title = String.valueOf(runtimeService.getVariable(task.getProcessInstanceId(), "title"));
            if (textTitle.equals(title)) {
                return TaskDto.builder()
                        .taskId(task.getId())
                        .taskName(task.getName())
                        .build();
            }
        }
        return null;
    }
}
