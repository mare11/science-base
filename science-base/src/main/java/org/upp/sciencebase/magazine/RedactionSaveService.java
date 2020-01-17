package org.upp.sciencebase.magazine;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedactionSaveService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Saving magazine redaction for: {}", execution.getVariable("name"));
    }
}
