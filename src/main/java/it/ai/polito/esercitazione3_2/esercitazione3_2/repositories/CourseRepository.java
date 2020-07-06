package it.ai.polito.esercitazione3_2.esercitazione3_2.repositories;

import it.ai.polito.esercitazione3_2.esercitazione3_2.entities.Course;
import it.ai.polito.esercitazione3_2.esercitazione3_2.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    @Query("SELECT s FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name=:courseName")
    List<Student> getStudentsInTeams(String courseName);

    @Query("SELECT s FROM Student s INNER JOIN s.courses c WHERE c.name=:courseName AND " +
            "s.id NOT IN (select s1.id from Student s1 INNER JOIN s1.teams t INNER JOIN t.course c WHERE c.name=:courseName)")
    List<Student> getStudentsNotInTeams(String courseName);

}
