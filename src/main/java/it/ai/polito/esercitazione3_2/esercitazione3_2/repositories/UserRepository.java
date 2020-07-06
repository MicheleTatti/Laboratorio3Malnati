package it.ai.polito.esercitazione3_2.esercitazione3_2.repositories;


import it.ai.polito.esercitazione3_2.esercitazione3_2.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}
