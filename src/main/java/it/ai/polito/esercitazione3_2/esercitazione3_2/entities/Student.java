package it.ai.polito.esercitazione3_2.esercitazione3_2.entities;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Student {
    @CsvBindByName
    @Id
    String id;
    @CsvBindByName
    String name;
    @CsvBindByName
    String firstname;

    @JoinTable(name="student_course", joinColumns = @JoinColumn(name="student_id"),
            inverseJoinColumns = @JoinColumn(name="course_name") )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    List<Course> courses = new ArrayList<>();

    @ManyToMany(mappedBy = "members")
    List<Team> teams = new ArrayList<>();

    public void addCourse(Course c){
        if(!courses.contains(c)) {
            courses.add(c);
            c.getStudents().add(this);
        }
    }

    public void addTeam(Team t){
        if(!teams.contains(t)){
            teams.add(t);
            t.getMembers().add(this);
        }
    }

    public void removeTeam(Team t){
        teams.remove(t);
        t.getMembers().remove(this);
    }

    public void setId(String id){
        this.id = id.toLowerCase();
    }
}
