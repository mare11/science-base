package org.upp.sciencebase.text;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.upp.sciencebase.dto.FormFieldDto;
import org.upp.sciencebase.dto.FormFieldsDto;
import org.upp.sciencebase.dto.FormSubmissionDto;
import org.upp.sciencebase.exception.BadRequestException;
import org.upp.sciencebase.model.Magazine;
import org.upp.sciencebase.model.ScienceArea;
import org.upp.sciencebase.model.Text;
import org.upp.sciencebase.repository.MagazineRepository;
import org.upp.sciencebase.repository.TextRepository;
import org.upp.sciencebase.service.UserTaskService;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.upp.sciencebase.util.ProcessUtil.NEW_TEXT_PROCESS_KEY;
import static org.upp.sciencebase.util.ProcessUtil.SCIENCE_AREA_FIELD;

@Slf4j
@Service
public class TextService {

    private final TaskService taskService;
    private final FormService formService;
    private final UserTaskService userTaskService;
    private final TextRepository textRepository;
    private final MagazineRepository magazineRepository;

    @Autowired
    public TextService(TaskService taskService, FormService formService, UserTaskService userTaskService, TextRepository textRepository, MagazineRepository magazineRepository) {
        this.taskService = taskService;
        this.formService = formService;
        this.userTaskService = userTaskService;
        this.textRepository = textRepository;
        this.magazineRepository = magazineRepository;
    }

    public FormFieldsDto startProcess(String magazineName, String username) {
        FormFieldsDto formFieldsDto = userTaskService.startProcessAndGetFormFields(NEW_TEXT_PROCESS_KEY, username);
        Task previousTask = taskService.createTaskQuery()
                .taskId(formFieldsDto.getTaskId())
                .singleResult();

        FormFieldDto fieldDto = formFieldsDto.getFormFields().get(0);
        FormSubmissionDto submissionDto = FormSubmissionDto.builder()
                .fieldId(fieldDto.getId())
                .fieldValue(magazineName)
                .build();
        userTaskService.submitForm(Collections.singletonList(submissionDto), formFieldsDto.getTaskId());

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
        return FormFieldsDto.builder()
                .processInstanceId(newTask.getProcessInstanceId())
                .taskId(newTask.getId())
                .formFields(taskFormData.getFormFields()
                        .stream()
                        .map(FormFieldDto::new)
                        .collect(Collectors.toList()))
                .build();
    }

    public Resource getTextFile(String fileName) {
        log.info("Reading file: {}", fileName);
        Text text = textRepository.findByFileName(fileName);
        if (ObjectUtils.isEmpty(text)) {
            throw new BadRequestException();
        }
        return new ByteArrayResource(text.getContent());
    }

    public void submitTextFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        try {
            Text text = Text.builder()
                    .fileName(fileName)
                    .content(file.getBytes())
                    .build();
            textRepository.save(text);
        } catch (IOException e) {
            log.info("Error while reading file: {}", fileName);
        }
        log.info("Submitted text file: {}", fileName);
    }
}
