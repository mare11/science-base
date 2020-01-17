package org.upp.sciencebase.magazine;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.repository.PaymentMethodRepository;

@Slf4j
@Service
public class MagazineInitHandler implements TaskListener {

    private final FormService formService;
    private final PaymentMethodRepository paymentMethodRepository;

    @Autowired
    public MagazineInitHandler(FormService formService, PaymentMethodRepository paymentMethodRepository) {
        this.formService = formService;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public void notify(DelegateTask task) {
//        log.info("Initializing payment method fields for magazine process!");
//        Map<String, String> paymentMethods = paymentMethodRepository.findAll().stream()
//                .collect(Collectors.toMap(PaymentMethod::getMethodKey, PaymentMethod::getMethodValue));
//
//        for (FormField formField : formService.getTaskFormData(task.getId()).getFormFields()) {
//            if (PAYMENT_METHOD_FIELD.equals(formField.getId())) {
//                Map<String, String> values = ((EnumFormType) formField.getType()).getValues();
//                paymentMethods.forEach(values::put);
//            }
//        }
    }
}
