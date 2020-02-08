package org.upp.sciencebase.text;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upp.sciencebase.dto.UserDto;
import org.upp.sciencebase.model.User;
import org.upp.sciencebase.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.upp.sciencebase.model.UserRoleEnum.EDITOR;
import static org.upp.sciencebase.util.ProcessUtil.SCIENCE_AREA_FIELD;

@Slf4j
@Service
public class GetScienceAreaRedactionService implements JavaDelegate {

    private final UserRepository userRepository;

    @Autowired
    public GetScienceAreaRedactionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String areaKey = execution.getVariable(SCIENCE_AREA_FIELD).toString();
        Set<User> editors = userRepository.findByUserEnabledTrueAndRoleAndScienceAreas_AreaKeyIn(EDITOR, Collections.singletonList(areaKey));
        Set<User> reviewers = userRepository.findByUserEnabledTrueAndReviewerTrueAndReviewerEnabledTrueAndScienceAreas_AreaKeyIn(Collections.singletonList(areaKey));

        String editorForArea = ObjectUtils.isEmpty(editors) ?
                execution.getVariable("mainEditor").toString() : editors.iterator().next().getUsername();
        execution.setVariable("editorForArea", editorForArea);

        List<UserDto> reviewersForArea = Optional.ofNullable(reviewers)
                .orElse(Collections.emptySet())
                .stream()
                .map(reviewer ->
                        UserDto.builder()
                                .username(reviewer.getUsername())
                                .fullName(reviewer.getFullName())
                                .build())
                .collect(Collectors.toList());
        execution.setVariable("reviewersForArea", reviewersForArea);
        execution.setVariable("hasReviewers", !reviewersForArea.isEmpty());
    }
}
