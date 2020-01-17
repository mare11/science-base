package org.upp.sciencebase.magazine;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.model.Magazine;
import org.upp.sciencebase.model.User;
import org.upp.sciencebase.repository.MagazineRepository;
import org.upp.sciencebase.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.upp.sciencebase.util.ProcessUtil.SELECTED_EDITORS_FIELD;
import static org.upp.sciencebase.util.ProcessUtil.SELECTED_REVIEWERS_FIELD;

@Slf4j
@Service
public class RedactionSaveService implements JavaDelegate {

    private final MagazineRepository magazineRepository;
    private final UserRepository userRepository;

    @Autowired
    public RedactionSaveService(MagazineRepository magazineRepository, UserRepository userRepository) {
        this.magazineRepository = magazineRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String magazineName = execution.getVariable("name").toString();
        Magazine magazine = magazineRepository.findByName(magazineName);
        log.info("Saving magazine redaction for: {}", magazine.getName());

        List<String> editors = (List<String>) execution.getVariable(SELECTED_EDITORS_FIELD);
        Set<User> editorSet = new HashSet<>();
        editors.forEach(username ->
                editorSet.add(userRepository.findByUsername(username))
        );
        magazine.setAreaEditors(editorSet);

        List<String> reviewers = (List<String>) execution.getVariable(SELECTED_REVIEWERS_FIELD);
        Set<User> reviewerSet = new HashSet<>();
        reviewers.forEach(username ->
                reviewerSet.add(userRepository.findByUsername(username))
        );
        magazine.setAreaReviewers(reviewerSet);

        magazineRepository.save(magazine);
    }
}
