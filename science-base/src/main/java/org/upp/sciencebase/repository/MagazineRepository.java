package org.upp.sciencebase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upp.sciencebase.model.Magazine;

import java.util.List;

@Repository
public interface MagazineRepository extends JpaRepository<Magazine, Long> {

    Magazine findByName(String name);

    Magazine findByIssn(String issn);

    Magazine findByNameOrIssn(String name, String issn);

    List<Magazine> findAllByMainEditor_Username(String username);
}
