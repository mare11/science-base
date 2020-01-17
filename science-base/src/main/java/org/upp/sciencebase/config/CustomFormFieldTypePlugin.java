package org.upp.sciencebase.config;

import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.context.annotation.Configuration;
import org.upp.sciencebase.util.EmailFormType;
import org.upp.sciencebase.util.MultiEnumFormType;
import org.upp.sciencebase.util.PasswordFormType;

import java.util.ArrayList;
import java.util.HashMap;

@Configuration
public class CustomFormFieldTypePlugin extends AbstractProcessEnginePlugin {

    private ProcessEngineConfigurationImpl processEngineConfiguration;

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        this.processEngineConfiguration = processEngineConfiguration;
        if (processEngineConfiguration.getCustomFormTypes() == null) {
            processEngineConfiguration.setCustomFormTypes(new ArrayList<>());
        }
        processEngineConfiguration.getCustomFormTypes().add(new MultiEnumFormType(new HashMap<>()));
        processEngineConfiguration.getCustomFormTypes().add(new EmailFormType());
        processEngineConfiguration.getCustomFormTypes().add(new PasswordFormType());
    }
}
