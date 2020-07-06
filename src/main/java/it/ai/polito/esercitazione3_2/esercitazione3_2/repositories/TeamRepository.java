package it.ai.polito.esercitazione3_2.esercitazione3_2.repositories;

import it.ai.polito.esercitazione3_2.esercitazione3_2.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
}
