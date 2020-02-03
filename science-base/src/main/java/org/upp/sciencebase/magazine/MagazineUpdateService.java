package org.upp.sciencebase.magazine;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.exception.BadRequestException;
import org.upp.sciencebase.exception.MagazineNameExistsException;
import org.upp.sciencebase.model.Magazine;
import org.upp.sciencebase.model.ScienceArea;
import org.upp.sciencebase.repository.MagazineRepository;
import org.upp.sciencebase.repository.ScienceAreaRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class MagazineUpdateService implements JavaDelegate {

    private final MagazineRepository magazineRepository;
    private final ScienceAreaRepository scienceAreaRepository;


    @Autowired
    public MagazineUpdateService(MagazineRepository magazineRepository, ScienceAreaRepository scienceAreaRepository) {
        this.magazineRepository = magazineRepository;
        this.scienceAreaRepository = scienceAreaRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String issn = execution.getVariable("issn").toString();
        String name = execution.getVariable("name").toString();
        List<String> areas = (List<String>) execution.getVariable("selectedAreas");

        log.info("Updating magazine: {}", name);
        Magazine magazine = magazineRepository.findByIssn(issn);
        if (magazine == null) {
            throw new BadRequestException();
        }

        if (magazineRepository.findByName(name) != null) {
            throw new MagazineNameExistsException(name, issn);
        }

        Set<ScienceArea> areaSet = new HashSet<>();
        areas.forEach(area ->
                areaSet.add(scienceAreaRepository.findByAreaKey(area))
        );

        magazine.setName(name);
        magazine.setScienceAreas(areaSet);
        magazineRepository.save(magazine);
        log.info("Magazine: {} updated!", name);
    }
}
