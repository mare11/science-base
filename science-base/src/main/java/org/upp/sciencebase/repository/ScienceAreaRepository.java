package org.upp.sciencebase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upp.sciencebase.model.ScienceArea;

@Repository
public interface ScienceAreaRepository extends JpaRepository<ScienceArea, Long> {

    ScienceArea findByAreaKey(String key);
}
