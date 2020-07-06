package it.ai.polito.esercitazione3_2.esercitazione3_2.controllers;


import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.CourseDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.StudentDTO;
import org.springframework.hateoas.Link;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


public class ModelHelper {

    public static CourseDTO enrich(CourseDTO c){
        String courseName = c.getName();
        Link selfLink = linkTo(CourseController.class).slash(courseName).withSelfRel();
        Link enrolledLink = linkTo(CourseController.class).slash(courseName).slash("enrolled").withRel("enrolled");
        c.add(enrolledLink);
        c.add(selfLink);
        return c;
    }


    public static StudentDTO enrich(StudentDTO s){
        String studentId = s.getId();
        Link selfLink = linkTo(StudentController.class).slash(studentId).withSelfRel();
        return s.add(selfLink);
    }

}
