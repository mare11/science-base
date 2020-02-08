package org.upp.sciencebase.text;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import static org.upp.sciencebase.util.ProcessUtil.SELECTED_MAG_FIELD;

@Slf4j
@Service
public class PayMembershipService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String magazineName = String.valueOf(execution.getVariable(SELECTED_MAG_FIELD));
        log.info("Paying membership for magazine???!!!: {}", magazineName);
    }
}
