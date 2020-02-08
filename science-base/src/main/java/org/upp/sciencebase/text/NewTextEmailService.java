package org.upp.sciencebase.text;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.model.Magazine;
import org.upp.sciencebase.model.Text;
import org.upp.sciencebase.repository.MagazineRepository;
import org.upp.sciencebase.repository.TextRepository;
import org.upp.sciencebase.util.EmailService;

import static org.upp.sciencebase.util.ProcessUtil.SELECTED_MAG_FIELD;

@Slf4j
@Service
public class NewTextEmailService implements JavaDelegate {

    private final EmailService emailService;
    private final TextRepository textRepository;
    private final MagazineRepository magazineRepository;

    @Autowired
    public NewTextEmailService(EmailService emailService, TextRepository textRepository, MagazineRepository magazineRepository) {
        this.emailService = emailService;
        this.textRepository = textRepository;
        this.magazineRepository = magazineRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String textTitle = execution.getVariable("title").toString();
        String magazineName = execution.getVariable(SELECTED_MAG_FIELD).toString();

        Text text = textRepository.findByTitle(textTitle);
        Magazine magazine = magazineRepository.findByName(magazineName);

//        emailService.sendNewTextNotificationMail(text.getAuthor(), text);
//        emailService.sendNewTextNotificationMail(magazine.getMainEditor(), text);
    }
}
