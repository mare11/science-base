package org.upp.sciencebase.magazine;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.impl.form.type.EnumFormType;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.dto.FormFieldDto;
import org.upp.sciencebase.dto.FormSubmissionDto;
import org.upp.sciencebase.dto.MagazineDto;
import org.upp.sciencebase.dto.TaskDto;
import org.upp.sciencebase.model.Magazine;
import org.upp.sciencebase.model.PaymentMethod;
import org.upp.sciencebase.model.ScienceArea;
import org.upp.sciencebase.model.User;
import org.upp.sciencebase.repository.MagazineRepository;
import org.upp.sciencebase.repository.ScienceAreaRepository;
import org.upp.sciencebase.repository.UserRepository;
import org.upp.sciencebase.service.UserTaskService;
import org.upp.sciencebase.util.MultiEnumFormType;

import java.util.*;
import java.util.stream.Collectors;

import static org.upp.sciencebase.model.UserRoleEnum.EDITOR;
import static org.upp.sciencebase.util.ProcessUtil.*;

@Slf4j
@Service
public class MagazineService {

    private final UserTaskService userTaskService;
    private final TaskService taskService;
    private final FormService formService;
    private final RuntimeService runtimeService;
    private final UserRepository userRepository;
    private final MagazineRepository magazineRepository;
    private final ScienceAreaRepository scienceAreaRepository;

    @Autowired
    public MagazineService(FormService formService, TaskService taskService, UserTaskService userTaskService, RuntimeService runtimeService, UserRepository userRepository, MagazineRepository magazineRepository, ScienceAreaRepository scienceAreaRepository) {
        this.formService = formService;
        this.taskService = taskService;
        this.userTaskService = userTaskService;
        this.runtimeService = runtimeService;
        this.userRepository = userRepository;
        this.magazineRepository = magazineRepository;
        this.scienceAreaRepository = scienceAreaRepository;
    }

    public List<MagazineDto> getAllMagazines() {
        return magazineRepository.findAll().stream()
                .filter(Magazine::isEnabled)
                .map(magazine ->
                        MagazineDto.builder()
                                .name(magazine.getName())
                                .issn(magazine.getIssn())
                                .paymentMethod(magazine.getPaymentMethod())
                                .enabled(magazine.isEnabled())
                                .build())
                .collect(Collectors.toList());
    }

    public TaskDto startProcess(String username) {
        TaskDto fieldsDto = userTaskService.startProcessAndGetFormFields(NEW_MAGAZINE_PROCESS_KEY, username);
        log.info("Initializing science areas and payment method fields for new magazine process!");

        Map<String, String> scienceAreas = scienceAreaRepository.findAll().stream()
                .collect(Collectors.toMap(ScienceArea::getAreaKey, ScienceArea::getAreaValue));
        Map<String, String> paymentMethods = Arrays.stream(PaymentMethod.values())
                .collect(Collectors.toMap(PaymentMethod::name, PaymentMethod::getLabel));

        fieldsDto.getFormFields().forEach(formField -> {
            if (SELECTED_AREAS_FIELD.equals(formField.getId())) {
                Map<String, String> values = new HashMap<>();
                scienceAreas.forEach(values::put);
                formField.setType(new MultiEnumFormType(values));
            }
            if (PAYMENT_METHOD_FIELD.equals(formField.getId())) {
                Map<String, String> values = ((EnumFormType) formField.getType()).getValues();
                paymentMethods.forEach(values::put);
            }
        });
        return fieldsDto;
    }

    public TaskDto submitFormAndGetNextTaskData(List<FormSubmissionDto> submittedFields, String taskId) {
        Task previousTask = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        userTaskService.submitForm(submittedFields, previousTask.getId());
        Task newTask = taskService.createTaskQuery()
                .processInstanceId(previousTask.getProcessInstanceId())
                .active()
                .singleResult();

        if (newTask.getAssignee().equals(previousTask.getAssignee())) {
            Map<String, Object> variables = runtimeService.getVariables(newTask.getProcessInstanceId());
            List<String> selectedAreas = (List<String>) variables.get("selectedAreas");
            List<FormFieldDto> formFields = new ArrayList<>();
            TaskFormData taskFormData = formService.getTaskFormData(newTask.getId());
            taskFormData.getFormFields().forEach(formField -> {
                FormFieldDto fieldDto = new FormFieldDto(formField);
                if (SELECTED_EDITORS_FIELD.equals(formField.getId())) {
                    Map<String, String> values = new HashMap<>();
                    Set<User> editors = userRepository.findByUserEnabledTrueAndRoleAndScienceAreas_AreaKeyIn(EDITOR, selectedAreas);
                    editors.forEach(editor ->
                            values.put(editor.getUsername(), editor.getFullName()));
                    fieldDto.setType(new MultiEnumFormType(values));
                }
                if (SELECTED_REVIEWERS_FIELD.equals(formField.getId())) {
                    Map<String, String> values = new HashMap<>();
                    Set<User> reviewers = userRepository.findByUserEnabledTrueAndReviewerTrueAndReviewerEnabledTrueAndScienceAreas_AreaKeyIn(selectedAreas);
                    reviewers.forEach(reviewer ->
                            values.put(reviewer.getUsername(), reviewer.getFullName()));
                    fieldDto.setType(new MultiEnumFormType(values));
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
}
