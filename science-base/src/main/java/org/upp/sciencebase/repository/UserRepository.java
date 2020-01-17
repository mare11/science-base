package org.upp.sciencebase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upp.sciencebase.model.User;
import org.upp.sciencebase.model.UserRoleEnum;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByRole(UserRoleEnum role);

    List<User> findByUserEnabledTrueAndRoleAndScienceAreas_AreaKeyIn(UserRoleEnum role, List<String> areaKeys);

    List<User> findByUserEnabledTrueAndReviewerTrueAndReviewerEnabledTrueAndScienceAreas_AreaKeyIn(List<String> areaKeys);
}
