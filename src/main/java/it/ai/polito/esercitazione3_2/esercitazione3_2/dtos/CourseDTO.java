package it.ai.polito.esercitazione3_2.esercitazione3_2.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;


@Data
public class CourseDTO extends RepresentationModel<CourseDTO> {

    String name;
    int min;
    int max;
    boolean enabled;
}
