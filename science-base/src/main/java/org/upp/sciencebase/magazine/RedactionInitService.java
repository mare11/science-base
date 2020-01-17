package org.upp.sciencebase.magazine;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedactionInitService implements TaskListener {

    private final FormService formService;

    @Autowired
    public RedactionInitService(FormService formService) {
        this.formService = formService;
    }

    @Override
    public void notify(DelegateTask task) {
//        log.info("Initializing editors and reviewers fields for new magazine process!");
//        Map<String, String> scienceAreas = scienceAreaRepository.findAll().stream()
//                .collect(Collectors.toMap(ScienceArea::getAreaKey, ScienceArea::getAreaValue));
//        Map<String, String> paymentMethods = paymentMethodRepository.findAll().stream()
//                .collect(Collectors.toMap(PaymentMethod::getMethodKey, PaymentMethod::getMethodValue));

//        for (FormField formField : formService.getTaskFormData(task.getId()).getFormFields()) {
//            if (SELECTED_EDITORS_FIELD.equals(formField.getId())) {
//                Map<String, String> values = ((MultiEnumFormType) formField.getType()).getValues();
////                scienceAreas.forEach(values::put);
//                values.put("admin", "Admin");
//            }
//            if (SELECTED_REVIEWERS_FIELD.equals(formField.getId())) {
//                Map<String, String> values = ((MultiEnumFormType) formField.getType()).getValues();
////                paymentMethods.forEach(values::put);
//                values.put("admin", "Admin");
//            }
//        }
    }
}
