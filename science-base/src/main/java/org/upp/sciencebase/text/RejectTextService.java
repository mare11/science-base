package org.upp.sciencebase.text;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.model.Text;
import org.upp.sciencebase.repository.TextRepository;
import org.upp.sciencebase.util.EmailService;

@Slf4j
@Service
public class RejectTextService implements JavaDelegate {

    private final EmailService emailService;
    private final TextRepository textRepository;

    @Autowired
    public RejectTextService(EmailService emailService, TextRepository textRepository) {
        this.emailService = emailService;
        this.textRepository = textRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String title = execution.getVariable("title").toString();
        Text text = textRepository.findByTitle(title);
        text.setRejected(true);
        textRepository.save(text);
        log.info("Text: {} rejected!", title);
        emailService.sendFinalTextNotificationMail(text.getAuthor(), text, false);
    }
}
