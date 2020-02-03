package org.upp.sciencebase.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.dto.FormFieldDto;
import org.upp.sciencebase.dto.FormFieldsDto;
import org.upp.sciencebase.dto.MagazineDto;
import org.upp.sciencebase.model.ScienceArea;
import org.upp.sciencebase.repository.MagazineRepository;
import org.upp.sciencebase.repository.ScienceAreaRepository;
import org.upp.sciencebase.util.MultiEnumFormType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.upp.sciencebase.util.ProcessUtil.NEW_MAGAZINE_PROCESS_KEY;
import static org.upp.sciencebase.util.ProcessUtil.SELECTED_AREAS_FIELD;

@Slf4j
@Service
public class EditorService {

    private final UserTaskService userTaskService;
    private final TaskService taskService;
    private final FormService formService;
    private final RuntimeService runtimeService;
    private final ScienceAreaRepository scienceAreaRepository;
    private final MagazineRepository magazineRepository;

    @Autowired
    public EditorService(UserTaskService userTaskService, TaskService taskService, FormService formService, RuntimeService runtimeService, ScienceAreaRepository scienceAreaRepository, MagazineRepository magazineRepository) {
        this.userTaskService = userTaskService;
        this.taskService = taskService;
        this.formService = formService;
        this.runtimeService = runtimeService;
        this.scienceAreaRepository = scienceAreaRepository;
        this.magazineRepository = magazineRepository;
    }

    public List<MagazineDto> getMagazines(String username) {
        return magazineRepository.findAllByMainEditor_Username(username).stream()
                .map(magazine ->
                        MagazineDto.builder()
                                .name(magazine.getName())
                                .issn(magazine.getIssn())
                                .paymentMethod(magazine.getPaymentMethod().getMethodValue())
                                .enabled(magazine.isEnabled())
                                .taskId(getCorrectionTaskIdForMagazine(username, magazine.getName()))
                                .build())
                .collect(Collectors.toList());
    }


    public FormFieldsDto getCorrectionTaskFormFields(String taskId) {
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
        return FormFieldsDto.builder()
                .processInstanceId(task.getProcessInstanceId())
                .taskId(task.getId())
                .formFields(formFieldDtos)
                .build();
    }

    private String getCorrectionTaskIdForMagazine(String username, String magazineName) {
        List<Task> userTasks = userTaskService.getUserTasksForSpecificProcess(username, NEW_MAGAZINE_PROCESS_KEY);
        for (Task task : userTasks) {
            String name = runtimeService.getVariable(task.getProcessInstanceId(), "name").toString();
            if (magazineName.equals(name)) {
                return task.getId();
            }
        }
        return null;
    }
}
