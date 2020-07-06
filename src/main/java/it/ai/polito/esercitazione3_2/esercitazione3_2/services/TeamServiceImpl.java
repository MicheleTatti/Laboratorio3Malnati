package it.ai.polito.esercitazione3_2.esercitazione3_2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.CourseDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.ProfessorDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.StudentDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.TeamDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.entities.*;
import it.ai.polito.esercitazione3_2.esercitazione3_2.exceptions.*;
import it.ai.polito.esercitazione3_2.esercitazione3_2.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;



    @Override
    public boolean addCourse(CourseDTO course) {

        if (courseRepository.existsById(course.getName()))  {
            return false;
        } else{
            Course c = modelMapper.map(course, Course.class);
            courseRepository.save(c);
            return true;
        }

    }

    @Override
    public Optional<CourseDTO> getCourse(String name) {
        if(!courseRepository.existsById(name))
            return Optional.empty();
        Course c = courseRepository.getOne(name);

        return Optional.of(modelMapper.map(c, CourseDTO.class));
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public boolean addStudent(StudentDTO student) {

        if (studentRepository.existsById(student.getId()) || userRepository.existsById(student.getId())) {
            return false;
        } else{
            Student s = modelMapper.map(student, Student.class);
            studentRepository.save(s);
            userRepository.save(User.builder()
                    .username(student.getId())
                    .password(passwordEncoder.encode(s.getId()))
                    .roles(Arrays.asList( "ROLE_STUDENT"))
                    .build());
            return true;
        }
    }

    @Override
    public Optional<StudentDTO> getStudent(String studentId) {

        if(!studentRepository.existsById(studentId))
            return Optional.empty();
        Student s =studentRepository.getOne(studentId);

        return Optional.of(modelMapper.map(s, StudentDTO.class));
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getEnrolledStudents(String courseName) {
        if(!courseRepository.existsById(courseName)) throw new CourseNotFoundException();
        Course c = courseRepository.getOne(courseName);


        return c.getStudents().stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addStudentToCourse(String studentId, String courseName) {

        if(!studentRepository.existsById(studentId)) throw new StudentNotFoundException();
        if(!courseRepository.existsById(courseName)) throw new CourseNotFoundException();

        Student s = studentRepository.getOne(studentId);
        Course c = courseRepository.getOne(courseName);

        if(c.getStudents().contains(s))
            return false;

        c.addStudent(s);
        return true;


    }

    @Override
    public void enableCourse(String courseName) {
        if(!courseRepository.existsById(courseName)) throw new CourseNotFoundException();
        Course c = courseRepository.getOne(courseName);
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!professorRepository.existsById(id)) throw new ProfessorNotFoundException();
        if(c.getProfessor() == null) {
            Professor p = professorRepository.getOne(id);
            c.setProfessor(p);
        }
        if(!c.getProfessor().getId().equals(id)) throw new ProfessorNotAllowedException();
        c.setEnabled(true);
    }

    @Override
    public void disableCourse(String courseName) {

        if(!courseRepository.existsById(courseName)) throw new CourseNotFoundException();

        Course c = courseRepository.getOne(courseName);
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!professorRepository.existsById(id)) throw new ProfessorNotFoundException();
        if(c.getProfessor() == null) {
            Professor p = professorRepository.getOne(id);
            c.setProfessor(p);
        }
        if(!c.getProfessor().getId().equals(id)) throw new ProfessorNotAllowedException();

        c.setEnabled(false);
    }

    @Override
    public List<Boolean> addAll(List<StudentDTO> students) {

        return students.stream().map(this::addStudent).collect(Collectors.toList());

    }

    @Override
    public List<Boolean> enrollAll(List<String> studentIds, String courseName) {

        String c = courseName.toLowerCase();
        return studentIds.stream().map(s -> addStudentToCourse(s,c)).collect(Collectors.toList());

    }

    @Override
    public List<Boolean> addAndEnroll(Reader r, String courseName) {

        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder(r).withType(StudentDTO.class).withIgnoreLeadingWhiteSpace(true).build();
        List<StudentDTO> students = csvToBean.parse();
        this.addAll(students);
        List<String> studentsId = students.stream().map(StudentDTO::getId).collect(Collectors.toList());
        return this.enrollAll(studentsId,courseName);

    }

    @Override
    public List<CourseDTO> getCourses(String studentId) {

        if(!studentRepository.existsById(studentId)) throw new StudentNotFoundException();
        Student s = studentRepository.getOne(studentId);

        return s.getCourses().stream().map(c -> modelMapper.map(c,CourseDTO.class)).collect(Collectors.toList());

    }

    @Override
    public List<TeamDTO> getTeamsForStudent(String studentId) {
        if(!studentRepository.existsById(studentId)) throw new StudentNotFoundException();
        Student s = studentRepository.getOne(studentId);
        return s.getTeams().stream()
                .map(t -> modelMapper.map(t,TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getMembers(Long teamId) {
        Team t = teamRepository.getOne(teamId);
        return t.getMembers().stream()
                .map( s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public TeamDTO proposeTeam(String courseId, String name, List<String> memberIds) {


        if(!courseRepository.existsById(courseId)) throw new CourseNotFoundException();

        Course c = courseRepository.getOne(courseId);

        if(!c.isEnabled()) throw new CourseNotEnabledException();

        if(!studentRepository.findAll().stream()
                .map(Student::getId)
                .collect(Collectors.toList())
                .containsAll(memberIds)) throw new StudentNotFoundException();

        if(!memberIds.contains(SecurityContextHolder.getContext().getAuthentication().getName())) throw new StudentNotFoundException();

        if(!c.getStudents().stream().map(Student::getId)
                .collect(Collectors.toList())
                .containsAll(memberIds)) throw new StudentNotEnrolledException();

        List<Student> students = studentRepository.findAll().stream()
                .filter( s -> memberIds.contains(s.getId()))
                .collect(Collectors.toList());

        for( Student s : students){

        if( courseRepository.getStudentsInTeams(courseId).contains(s) ) throw new StudentAlreadyBusyException();
        }
        if( memberIds.size() < c.getMin() ||  memberIds.size() > c.getMax() ) throw new CourseCardinalityLimitException();

        if( students.size() != memberIds.size())  throw new StudentAlreadyPresentException();


        Team team = new Team();
        team.setName(name);
        team.setCourse(c);
        for (Student s:students ) {
            team.addMembers(s);
        }
        teamRepository.save(team);


        return modelMapper.map(team, TeamDTO.class);
    }

    @Override
    public List<TeamDTO> getTeamForCourse(String courseName) {

        if(!courseRepository.existsById(courseName)) throw new CourseNotFoundException();
        Course c = courseRepository.getOne(courseName);

        return teamRepository.findAll().stream()
                .filter(t -> t.getCourse().equals(c)).map(t -> modelMapper.map(t, TeamDTO.class)).collect(Collectors.toList());

    }

    @Override
    public List<StudentDTO> getStudentsInTeams(String courseName) {
        return courseRepository.getStudentsInTeams(courseName).stream()
                .map( s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getAvailableStudents(String courseName) {

          return courseRepository.getStudentsNotInTeams(courseName).stream()
                .map( s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean evictTeam(Long teamID) {

        if(!teamRepository.existsById(teamID)) return false;

        Team t = teamRepository.getOne(teamID);
        Course c = t.getCourse();
        List<Student> students = t.getMembers();
        students.forEach( s -> s.removeTeam(t));
        c.removeTeam(t);
        teamRepository.delete(t);
        return  true;
    }

    @Override
    public Boolean setStatus(Long teamId) {
        if (!teamRepository.existsById(teamId) ) return false;
        Team t = teamRepository.getOne(teamId);
        t.setStatus(1);
        return true;
    }

    @Override
    public boolean addProfessor(ProfessorDTO professor) {
        if (professorRepository.existsById(professor.getId()) || userRepository.existsById(professor.getId())) {
            return false;
        } else{
            Professor p = modelMapper.map(professor, Professor.class);
            professorRepository.save(p);
            userRepository.save(User.builder()
                    .username(professor.getId())
                    .password(passwordEncoder.encode("password"))
                    .roles(Arrays.asList( "ROLE_TEACHER"))
                    .build());
            return true;
        }
    }

    @Override
    public CourseDTO setCourseProfessor(String name, String id) {

        if(!courseRepository.existsById(name)) throw new CourseNotFoundException();
        if(!professorRepository.existsById(id) || !userRepository.existsById(id)) throw new ProfessorNotFoundException();

        Course c = courseRepository.getOne(name);

        c.setProfessor(professorRepository.getOne(id));

        return modelMapper.map(c, CourseDTO.class);
    }

    @Override
    public List<ProfessorDTO> getAllProfessors() {
        return professorRepository.findAll()
                .stream()
                .map(p -> modelMapper.map(p, ProfessorDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getProfessorCourses(String id) {
        if(!professorRepository.existsById(id)) throw new ProfessorNotFoundException();
        return professorRepository.getOne(id).getCourses().stream().map( c -> modelMapper.map(c, CourseDTO.class)).collect(Collectors.toList());
    }

}
