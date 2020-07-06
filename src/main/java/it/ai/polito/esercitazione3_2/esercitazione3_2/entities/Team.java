package it.ai.polito.esercitazione3_2.esercitazione3_2.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data

public class Team {

    @Id
    @GeneratedValue
    Long id;

    String name;

    int status;

    @ManyToOne
    @JoinColumn(name="course_id")
    Course course;

    @JoinTable(name="team_student", joinColumns = @JoinColumn(name="team_id"),
            inverseJoinColumns = @JoinColumn(name="student_id"))
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<Student> members = new ArrayList<Student>();

    public void setCourse(Course c){
        if (course != null) {
            course.removeTeam(this);
        }

        if(c != null) {
            course = c;
            c.getTeams().add(this);
        }else{
            course = null;
        }
    }

    public void addMembers(Student s){
        if(!members.contains(s)){
            members.add(s);
            s.getTeams().add(this);
        }
    }

    public void removeMembers(Student s){
        members.remove(s);
        s.getTeams().remove(this);
    }
}
