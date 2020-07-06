package it.ai.polito.esercitazione3_2.esercitazione3_2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Course {

    @Id
    String name;
    int min;
    int max;
    boolean enabled;

    @ManyToMany(mappedBy = "courses")
    List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    List<Team> teams;

    @ManyToOne
    @JoinColumn(name="professor_id")
    Professor professor;

    public void addStudent(Student s) {
        if (!students.contains(s)) {
            students.add(s);
            s.getCourses().add(this);
        }
    }

    public void addTeam(Team t){
        teams.add(t);
        t.setCourse(this);
    }

    public void setProfessor(Professor p){
            if (professor != null) {
                professor.removeCourse(this);
            }

            if(p != null) {
                professor = p;
                p.getCourses().add(this);
            }else{
                professor = null;
            }
        }

    public void removeTeam(Team t){
         teams.remove(t);
    }

    public void setName(String name){
        this.name = name.toLowerCase();
    }


}
