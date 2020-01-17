package org.upp.sciencebase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upp.sciencebase.model.Magazine;

@Repository
public interface MagazineRepository extends JpaRepository<Magazine, Long> {

    Magazine findByName(String name);

    Magazine findByNameAndIssn(String name, String issn);
}
