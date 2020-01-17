package org.upp.sciencebase.magazine;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.model.Magazine;
import org.upp.sciencebase.repository.MagazineRepository;

@Slf4j
@Service
public class MagazineActivationService implements JavaDelegate {

    private final MagazineRepository magazineRepository;

    @Autowired
    public MagazineActivationService(MagazineRepository magazineRepository) {
        this.magazineRepository = magazineRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Magazine magazine = magazineRepository.findByName(execution.getVariable("name").toString());
        log.info("Activating magazine: {}", magazine.getName());
        magazine.setEnabled(true);
        magazineRepository.save(magazine);
    }
}
