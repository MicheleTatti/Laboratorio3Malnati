package it.ai.polito.esercitazione3_2.esercitazione3_2.repositories;


import it.ai.polito.esercitazione3_2.esercitazione3_2.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {


    @Query("SELECT tkn FROM Token tkn WHERE tkn.expiryDate=:t")
    List<Token> findAllByExpiryBefore(Timestamp t);
    @Query("SELECT tkn FROM Token tkn WHERE tkn.teamId=:teamID")
    List<Token> findAllByTeamId(Long teamID);
}
