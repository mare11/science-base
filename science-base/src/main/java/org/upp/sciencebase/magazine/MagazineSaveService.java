package org.upp.sciencebase.magazine;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.exception.MagazineNameExistsException;
import org.upp.sciencebase.model.Magazine;
import org.upp.sciencebase.model.PaymentMethod;
import org.upp.sciencebase.model.ScienceArea;
import org.upp.sciencebase.repository.MagazineRepository;
import org.upp.sciencebase.repository.ScienceAreaRepository;
import org.upp.sciencebase.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.upp.sciencebase.util.ProcessUtil.PROCESS_INITIATOR_FIELD;

@Slf4j
@Service
public class MagazineSaveService implements JavaDelegate {

    private final UserRepository userRepository;
    private final ScienceAreaRepository scienceAreaRepository;
    private final MagazineRepository magazineRepository;

    @Autowired
    public MagazineSaveService(UserRepository userRepository, ScienceAreaRepository scienceAreaRepository, MagazineRepository magazineRepository) {
        this.userRepository = userRepository;
        this.scienceAreaRepository = scienceAreaRepository;
        this.magazineRepository = magazineRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Magazine magazine = extractMagazineVariables(execution);
        if (magazineRepository.findByNameOrIssn(magazine.getName(), magazine.getIssn()) != null) {
            throw new MagazineNameExistsException(magazine.getName(), magazine.getIssn());
        }
        magazineRepository.save(magazine);
        log.info("Saving magazine: {}", magazine.getName());
    }

    private Magazine extractMagazineVariables(DelegateExecution execution) {
        log.info("Execution variables:");
        execution.getVariables().forEach((s, o) ->
                log.info("Variable: {}, value: {}", s, o)
        );

        Magazine.MagazineBuilder magazineBuilder = Magazine.builder()
                .name(execution.getVariable("name").toString())
                .issn(execution.getVariable("issn").toString())
                .enabled(false);

        List<String> areas = (List<String>) execution.getVariable("selectedAreas");
        Set<ScienceArea> areaSet = new HashSet<>();
        areas.forEach(area ->
                areaSet.add(scienceAreaRepository.findByAreaKey(area))
        );
        magazineBuilder.scienceAreas(areaSet);

        String paymentMethod = execution.getVariable("paymentMethod").toString();
        magazineBuilder.paymentMethod(PaymentMethod.valueOf(paymentMethod));

        String mainEditor = execution.getVariable(PROCESS_INITIATOR_FIELD).toString();
        magazineBuilder.mainEditor(userRepository.findByUsername(mainEditor));

        return magazineBuilder.build();
    }
}
