package it.ai.polito.esercitazione3_2.esercitazione3_2.services;


import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.CourseDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.ProfessorDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.StudentDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.TeamDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.Reader;
import java.util.List;
import java.util.Optional;

public interface TeamService {

    boolean addCourse(CourseDTO course);
    Optional<CourseDTO> getCourse(String name);
    List<CourseDTO> getAllCourses();
    boolean addStudent(StudentDTO student);
    Optional<StudentDTO> getStudent(String studentId);
    List<StudentDTO> getAllStudents();
    List<StudentDTO> getEnrolledStudents(String courseName);
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    boolean addStudentToCourse(String studentId, String courseName);
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    void enableCourse(String courseName);
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    void disableCourse(String courseName);
    List<Boolean> addAll(List<StudentDTO> students);
    List<Boolean> enrollAll(List<String> studentIds, String courseName);
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    List<Boolean> addAndEnroll(Reader r, String courseName);
    List<CourseDTO> getCourses(String studentId);
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    List<TeamDTO> getTeamsForStudent(String studentId);
    List<StudentDTO>getMembers(Long TeamId);
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    TeamDTO proposeTeam(String courseId, String name, List<String> memberIds);
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    List<TeamDTO> getTeamForCourse(String courseName);
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    List<StudentDTO> getStudentsInTeams(String courseName);
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    List<StudentDTO> getAvailableStudents(String courseName);
    Boolean evictTeam(Long teamID);
    Boolean setStatus(Long teamId);
    boolean addProfessor(ProfessorDTO professor);
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    CourseDTO setCourseProfessor(String name, String id);
    List<ProfessorDTO> getAllProfessors();
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    List<CourseDTO> getProfessorCourses(String id);


}
