package org.upp.sciencebase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upp.sciencebase.model.Text;

@Repository
public interface TextRepository extends JpaRepository<Text, Long> {

    Text findByFileName(String fileName);
}
