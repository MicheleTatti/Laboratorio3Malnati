package it.ai.polito.esercitazione3_2.esercitazione3_2.repositories;


import it.ai.polito.esercitazione3_2.esercitazione3_2.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
}
