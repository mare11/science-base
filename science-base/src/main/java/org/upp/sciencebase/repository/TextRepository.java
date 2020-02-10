package org.upp.sciencebase.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upp.sciencebase.model.Text;

import java.util.List;

@Repository
public interface TextRepository extends JpaRepository<Text, Long> {

    Text findByTitle(String title);

    List<Text> findByMagazine_MainEditor_Username(String username);

    List<Text> findByAuthor_Username(String username);

    List<Text> findByAcceptedTrueAndMagazine_Name(String magazineName);

}
