package org.upp.sciencebase.registration;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.FormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.config.CustomFormFieldTypePlugin;
import org.upp.sciencebase.model.ScienceArea;
import org.upp.sciencebase.repository.ScienceAreaRepository;
import org.upp.sciencebase.util.MultiEnumFormType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegistrationInitHandler implements TaskListener {

    private final FormService formService;
    private final ScienceAreaRepository scienceAreaRepository;
    private final CustomFormFieldTypePlugin formFieldTypePlugin;

    @Autowired
    public RegistrationInitHandler(FormService formService, ScienceAreaRepository scienceAreaRepository, CustomFormFieldTypePlugin formFieldTypePlugin) {
        this.formService = formService;
        this.scienceAreaRepository = scienceAreaRepository;
        this.formFieldTypePlugin = formFieldTypePlugin;
    }

    @Override
    public void notify(DelegateTask task) {
        log.info("Initializing science areas field for registration!");
        Map<String, String> scienceAreas = scienceAreaRepository.findAll().stream()
                .collect(Collectors.toMap(ScienceArea::getAreaKey, ScienceArea::getAreaValue));
        for (FormField formField : formService.getTaskFormData(task.getId()).getFormFields()) {
            if (formField.getType() instanceof MultiEnumFormType) {
                FormType type = formField.getType();
                type = new MultiEnumFormType(new HashMap<>());
                Map<String, String> values = ((MultiEnumFormType) formField.getType()).getValues();
                scienceAreas.forEach(values::put);
            }
        }
    }
}
