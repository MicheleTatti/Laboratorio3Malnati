package it.ai.polito.esercitazione3_2.esercitazione3_2.dtos;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class ProfessorDTO  extends RepresentationModel<ProfessorDTO> {

    String id;
    String name;
    String lastname;

}
