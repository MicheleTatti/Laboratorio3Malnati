package it.ai.polito.esercitazione3_2.esercitazione3_2.dtos;

import lombok.Data;

@Data
public class UserDTO {

    Long id;
    private String username;
    private String password;
}
