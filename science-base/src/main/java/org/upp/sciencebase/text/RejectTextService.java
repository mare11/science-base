package org.upp.sciencebase.text;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RejectTextService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Text: {} rejected!", execution.getVariable("title"));
    }
}
