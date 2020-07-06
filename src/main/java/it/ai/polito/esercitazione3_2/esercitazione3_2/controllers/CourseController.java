package it.ai.polito.esercitazione3_2.esercitazione3_2.controllers;


import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.CourseDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.StudentDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.exceptions.ProfessorNotAllowedException;
import it.ai.polito.esercitazione3_2.esercitazione3_2.exceptions.ProfessorNotFoundException;
import it.ai.polito.esercitazione3_2.esercitazione3_2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/API/courses")
public class CourseController {

    @Autowired
    private TeamService tm;


    @GetMapping({"","/"})
    List<CourseDTO> all(){
        return tm.getAllCourses().stream().map(ModelHelper::enrich).collect(Collectors.toList());
    }

    @GetMapping(value = "/{name}", produces = {"application/hal+json"})
    CourseDTO getOne(@PathVariable String name){
        Optional<CourseDTO> courseDTO = tm.getCourse(name.toLowerCase());
        if( !courseDTO.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, name);
        return ModelHelper.enrich(courseDTO.get());
    }

    @GetMapping(value="/{name}/enrolled")
    List<StudentDTO> enrolledStudents(@PathVariable String name){
        if(!tm.getCourse(name).isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, name);
        return tm.getEnrolledStudents(name.toLowerCase());
    }

    @PostMapping({"","/"})
    CourseDTO addCourse(@RequestBody CourseDTO c){
        if (!tm.addCourse(c)) throw new ResponseStatusException(HttpStatus.CONFLICT,c.getName());
        return c;
    }

    @PostMapping("/{name}/enrollOne")
    StudentDTO enrollOne(@PathVariable String name, @RequestBody StudentDTO s){

        if(!tm.getCourse(name).isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Il corso:" + name +" non è stato trovato");
        tm.addStudentToCourse(s.getId(),name.toLowerCase());
        return s;
    }

    @PostMapping("/{name}/enrollMany")
    List<Boolean> enrollStudents(@PathVariable String name, @RequestParam("file") MultipartFile file) {

        if(!tm.getCourse(name).isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Il corso:" + name +" non è stato trovato");

        String type = file.getContentType();
       if(!type.equals("text/csv")) throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        try {
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            return tm.addAndEnroll( reader, name.toLowerCase());
        } catch (IOException e) {
           throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nell'inserimento");
        }
    }

    @PostMapping("/{name}/enable")
    void abilitaCorso(@PathVariable String name){
        if(!tm.getCourse(name).isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Il corso:" + name +" non è stato trovato");
        try{
            tm.enableCourse(name.toLowerCase());
        }catch(ProfessorNotFoundException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nella ricerca del professore");
        }catch(ProfessorNotAllowedException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Professore non abilitato");

        }
    }

    @PostMapping("/{name}/disable")
    void disabilitaCorso(@PathVariable String name){

        if(!tm.getCourse(name).isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Il corso:" + name +" non è stato trovato");
        try{
        tm.disableCourse(name.toLowerCase());
        }catch(ProfessorNotFoundException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nella ricerca del professore");
        }catch(ProfessorNotAllowedException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Professore non abilitato");
        }
    }


}
