package org.upp.sciencebase.text;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SaveTextService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Saving text...");
        log.info("Execution variables:");
        execution.getVariables().forEach((s, o) ->
                log.info("Variable: {}, value: {}", s, o)
        );
    }
}
