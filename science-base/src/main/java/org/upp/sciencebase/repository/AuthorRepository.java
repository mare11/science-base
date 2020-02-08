package org.upp.sciencebase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upp.sciencebase.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
