package org.upp.sciencebase.text;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.model.Author;
import org.upp.sciencebase.model.Text;
import org.upp.sciencebase.repository.AuthorRepository;
import org.upp.sciencebase.repository.TextRepository;

@Slf4j
@Service
public class SaveCoauthorService implements JavaDelegate {

    private final TextRepository textRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public SaveCoauthorService(TextRepository textRepository, AuthorRepository authorRepository) {
        this.textRepository = textRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Text text = textRepository.findByTitle(execution.getVariable("title").toString());
        Author coauthor = extractCoauthorVariables(execution);

        coauthor.setText(text);
        authorRepository.save(coauthor);
        log.info("Coauthor ({}) added to text (title: {})!", coauthor, text.getTitle());
    }

    private Author extractCoauthorVariables(DelegateExecution execution) {
        return Author.builder()
                .firstName(execution.getVariable("coauthorFirstName").toString())
                .lastName(execution.getVariable("coauthorLastName").toString())
                .email(execution.getVariable("coauthorEmail").toString())
                .country(execution.getVariable("coauthorCountry").toString())
                .city(execution.getVariable("coauthorCity").toString())
                .build();
    }
}
