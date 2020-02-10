package org.upp.sciencebase.text;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.exception.BadRequestException;
import org.upp.sciencebase.model.Magazine;
import org.upp.sciencebase.model.ScienceArea;
import org.upp.sciencebase.model.Text;
import org.upp.sciencebase.model.User;
import org.upp.sciencebase.repository.MagazineRepository;
import org.upp.sciencebase.repository.ScienceAreaRepository;
import org.upp.sciencebase.repository.TextRepository;
import org.upp.sciencebase.repository.UserRepository;

import static org.upp.sciencebase.util.ProcessUtil.PROCESS_INITIATOR_FIELD;
import static org.upp.sciencebase.util.ProcessUtil.SELECTED_MAG_FIELD;

@Slf4j
@Service
public class SaveTextService implements JavaDelegate {

    private final TextRepository textRepository;
    private final UserRepository userRepository;
    private final MagazineRepository magazineRepository;
    private final ScienceAreaRepository scienceAreaRepository;

    @Autowired
    public SaveTextService(TextRepository textRepository, UserRepository userRepository, MagazineRepository magazineRepository, ScienceAreaRepository scienceAreaRepository) {
        this.textRepository = textRepository;
        this.userRepository = userRepository;
        this.magazineRepository = magazineRepository;
        this.scienceAreaRepository = scienceAreaRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Text text = extractTextVariables(execution);
        if (textRepository.findByTitle(text.getTitle()) != null) {
            log.error("Text with title: {} already exists!", text.getTitle());
            throw new BadRequestException();
        }
        textRepository.save(text);
        log.info("Text (title: {}) saved!", text.getTitle());
    }

    private Text extractTextVariables(DelegateExecution execution) {
        log.info("Execution variables:");
        execution.getVariables().forEach((s, o) ->
                log.info("Variable: {}, value: {}", s, o)
        );

        Text.TextBuilder textBuilder = Text.builder()
                .title(execution.getVariable("title").toString())
                .keyTerms(execution.getVariable("keyTerms").toString())
                .apstract(execution.getVariable("apstract").toString())
                .accepted(false)
                .rejected(false);

        String areaKey = execution.getVariable("scienceArea").toString();
        ScienceArea area = scienceAreaRepository.findByAreaKey(areaKey);
        textBuilder.scienceArea(area);

        String authorUsername = execution.getVariable(PROCESS_INITIATOR_FIELD).toString();
        User author = userRepository.findByUsername(authorUsername);
        textBuilder.author(author);

        String magazineName = execution.getVariable(SELECTED_MAG_FIELD).toString();
        Magazine magazine = magazineRepository.findByName(magazineName);
        textBuilder.magazine(magazine);
        execution.setVariable("mainEditor", magazine.getMainEditor().getUsername());

        return textBuilder.build();
    }
}
