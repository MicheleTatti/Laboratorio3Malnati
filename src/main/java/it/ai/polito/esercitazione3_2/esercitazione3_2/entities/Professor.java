package it.ai.polito.esercitazione3_2.esercitazione3_2.entities;

import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
public class Professor {

    @Id
    String id;

    String name;

    String lastname;

    @OneToMany(mappedBy = "professor")
    List<Course> courses = new ArrayList<>();

    public void setId(String id){
        this.id = id.toLowerCase();
    }

    public void addCourse(Course c) {
            courses.add(c);
            c.setProfessor(this);
    }

    public void removeCourse(Course c){
        courses.remove(c);
    }

}
