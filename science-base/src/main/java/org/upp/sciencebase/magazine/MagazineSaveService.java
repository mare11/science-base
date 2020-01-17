package org.upp.sciencebase.magazine;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MagazineSaveService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
//        var main_editor = execution.getVariable("process_initiator");
//        execution.setVariable("main_editor", main_editor);
//        execution.setVariable("is_active", false);
//        execution.setVariable('areas',S('[]'));
//        execution.setVariable('display_areas','');
//        execution.setVariable("correction", false);

    }
}
