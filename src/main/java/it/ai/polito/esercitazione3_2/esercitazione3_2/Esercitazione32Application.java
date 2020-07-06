package it.ai.polito.esercitazione3_2.esercitazione3_2;

import it.ai.polito.esercitazione3_2.esercitazione3_2.entities.User;
import it.ai.polito.esercitazione3_2.esercitazione3_2.repositories.UserRepository;
import it.ai.polito.esercitazione3_2.esercitazione3_2.services.NotificationService;
import it.ai.polito.esercitazione3_2.esercitazione3_2.services.TeamService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@SpringBootApplication
public class Esercitazione32Application {


    @Bean
    ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    CommandLineRunner runner(NotificationService ns, TeamService ts){
        return new CommandLineRunner() {

            @Autowired
            UserRepository users;

            @Autowired
            PasswordEncoder passwordEncoder;

            @Override
            public void run(String... args) throws Exception {

                if(!users.existsById("admin"))
                this.users.save(User.builder()
                        .username("admin")
                        .password(this.passwordEncoder.encode("password"))
                        .roles(Arrays.asList("ROLE_ADMIN"))
                        .build()
                );

            }

        };
    }


    public static void main(String[] args) {
        SpringApplication.run(Esercitazione32Application.class, args);
    }

}
