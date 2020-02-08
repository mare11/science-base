package org.upp.sciencebase.text;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.exception.BadRequestException;
import org.upp.sciencebase.model.Magazine;
import org.upp.sciencebase.repository.MagazineRepository;

import static org.upp.sciencebase.model.PaymentMethod.AUTHORS;
import static org.upp.sciencebase.util.ProcessUtil.SELECTED_MAG_FIELD;

@Slf4j
@Service
public class CheckPaymentMethodService implements JavaDelegate {

    private final MagazineRepository magazineRepository;

    @Autowired
    public CheckPaymentMethodService(MagazineRepository magazineRepository) {
        this.magazineRepository = magazineRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String magazineName = String.valueOf(execution.getVariable(SELECTED_MAG_FIELD));
        log.info("Selected magazine with name: {}", magazineName);
        if (StringUtils.isBlank(magazineName)) {
            throw new BadRequestException();
        }

        Magazine magazine = magazineRepository.findByName(magazineName);
        if (ObjectUtils.isEmpty(magazine)) {
            throw new BadRequestException();
        }

        execution.setVariable("hasOpenAccess", hasOpenAccess(magazine));
        execution.setVariable("hasMembership", hasMembership());
        log.info("Found magazine: {} with payment method: {}", magazine.getName(), magazine.getPaymentMethod());
    }

    private boolean hasOpenAccess(Magazine magazine) {
        return AUTHORS == magazine.getPaymentMethod();
    }

    private boolean hasMembership() {
        return true;
    }
}
