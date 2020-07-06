package it.ai.polito.esercitazione3_2.esercitazione3_2.controllers;

import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.CourseDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.ProfessorDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.exceptions.CourseNotFoundException;
import it.ai.polito.esercitazione3_2.esercitazione3_2.exceptions.ProfessorNotFoundException;
import it.ai.polito.esercitazione3_2.esercitazione3_2.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/API/professors")
public class ProfessorController {

    @Autowired
    TeamService teamService;


    @GetMapping({"","/"})
    List<ProfessorDTO> all(){ return teamService.getAllProfessors();}

    /*
     * Viene creato un professore, e un utente con la password "password" di default per tutti gli utenti e il ruolo Teacher
     *
     */
    @PostMapping({"","/"})
    ProfessorDTO addProfessor(@RequestBody ProfessorDTO p){
        if (!teamService.addProfessor(p)) throw new ResponseStatusException(HttpStatus.CONFLICT,p.getName());
        return p;
    }

    /*
        Assegna al corso {name}, il professore con id passato
     */

    @PostMapping("/{name}")
    CourseDTO setProfessor(@PathVariable String name, @RequestParam("id") String id){

        try{
            return teamService.setCourseProfessor(name,id);
        }catch(CourseNotFoundException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Il corso " + name + " non esiste!");
        }catch(ProfessorNotFoundException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Il professore " + id + " non esiste!");
        }

    }

    /*
     * restituisce la lista dei Corsi assegnati al professore
     */
    @GetMapping("/{id}")
    List<CourseDTO> getProfessorCourses(@PathVariable String id){
        return  teamService.getProfessorCourses(id);
    }



}
