package it.ai.polito.esercitazione3_2.esercitazione3_2.controllers;


import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.CourseDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.StudentDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.TeamDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.exceptions.*;
import it.ai.polito.esercitazione3_2.esercitazione3_2.repositories.StudentRepository;
import it.ai.polito.esercitazione3_2.esercitazione3_2.services.NotificationService;
import it.ai.polito.esercitazione3_2.esercitazione3_2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/API/students")
public class StudentController {

    @Autowired
    private TeamService tm;

    @Autowired
    private NotificationService ns;

    @Autowired
    private StudentRepository sr;

    @GetMapping({"","/"})
    List<StudentDTO> all(){
        return tm.getAllStudents().stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}", produces = {"application/hal+json"})
    StudentDTO getOne(@PathVariable String id){
        Optional<StudentDTO> studentDTO = tm.getStudent(id);
        if( !studentDTO.isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT, id);
        return ModelHelper.enrich(studentDTO.get());
    }

    @PostMapping({"","/"})
    StudentDTO addStudent(@RequestBody StudentDTO s){
        if (!tm.addStudent(s)) throw new ResponseStatusException(HttpStatus.CONFLICT,s.getId());
        return s;
    }

    @PreAuthorize("#id == authentication.principal.username")
    @GetMapping(value = "/{id}/courses", produces = {"application/hal+json"})
    List<CourseDTO> getEnrolledCourses(@PathVariable String id){
        if( !sr.existsById(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, id);
        return tm.getCourses(id);
    }

    @PreAuthorize("#id == authentication.principal.username")
    @GetMapping(value = "/{id}/teams", produces = {"application/hal+json"})
    List<TeamDTO> getTeams(@PathVariable String id){
        if( !sr.existsById(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, id);
        return tm.getTeamsForStudent(id);
    }

    @GetMapping(value="/courses/{name}/teams")
    List<TeamDTO> enrolledTeams(@PathVariable String name){
        return tm.getTeamForCourse(name);
    }

    @GetMapping(value="/courses/{name}/available")
    List<StudentDTO> availableStudents(@PathVariable String name){
        return tm.getAvailableStudents(name);
    }

    @GetMapping(value="/courses/{name}/studentsInTeam")
    List<StudentDTO> studentsAlreadyInATeam(@PathVariable String name){
        return tm.getStudentsInTeams(name);
    }

    @PostMapping("/courses/{name}/proposeTeam")
    TeamDTO proposeTeam(@PathVariable String name, @RequestParam("teamName") String teamName, @RequestParam("members") List<String> memberIds) {
        try {
            TeamDTO team = tm.proposeTeam(name, teamName, memberIds);
            ns.notifyTeam(team, memberIds);
            return team;
        } catch (CourseNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Corso non presente");
        } catch (CourseNotEnabledException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Corso non abilitato");
        } catch (StudentNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Studente non presente");
        } catch (StudentNotEnrolledException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Studente non iscritto al corso");
        } catch (StudentAlreadyBusyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Studente già impegnato con un team");
        } catch (CourseCardinalityLimitException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Errore con la cardinalità dei team");
        } catch (StudentAlreadyPresentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Studente già presente");
        }
    }


}
