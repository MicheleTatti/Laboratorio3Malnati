package it.ai.polito.esercitazione3_2.esercitazione3_2.services;


import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.TeamDTO;
import it.ai.polito.esercitazione3_2.esercitazione3_2.entities.Token;
import it.ai.polito.esercitazione3_2.esercitazione3_2.exceptions.ConfirmationException;
import it.ai.polito.esercitazione3_2.esercitazione3_2.repositories.TeamRepository;
import it.ai.polito.esercitazione3_2.esercitazione3_2.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.*;

@Service
public class NotificationServiceImpl implements NotificationService{

    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamService teamService;

    @Override
    public void sendMessage(String address, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(address);
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);
    }

    @Override
    public boolean confirm(String token) {

        if(!tokenRepository.existsById(token)){
            return false;
        }
        Token t = tokenRepository.getOne(token);
        if( !t.getExpiryDate().after(new Timestamp(System.currentTimeMillis())))
            return false;

        tokenRepository.delete(t);
        Long teamId = t.getTeamId();

        List<Token> tokens = tokenRepository.findAllByTeamId(teamId);

        if(!tokens.isEmpty()) throw new ConfirmationException();

        return teamService.setStatus(teamId);

    }

    @Override
    public boolean reject(String token) {


        if(!tokenRepository.existsById(token)){
            return false;
        }

        Token t = tokenRepository.getOne(token);

        if( !t.getExpiryDate().after(new Timestamp(System.currentTimeMillis())))
            return false;

        List<Token> tokens = tokenRepository.findAllByTeamId(t.getTeamId());

        tokens.forEach(ts -> tokenRepository.delete(ts));

        teamService.evictTeam(t.getTeamId());

        return true;

    }



    @Override
    public void notifyTeam(TeamDTO dto, List<String> memberIds) {

        int oneHour = 60*60*1000;


        for (String sId:  memberIds) {
            String tokenString =  UUID.randomUUID().toString();

            Token s = new Token();
            s.setId(tokenString);
            s.setExpiryDate(new Timestamp(  Calendar.getInstance().getTimeInMillis() + oneHour));
            s.setTeamId(dto.getId());
            tokenRepository.save(s);

            String baseURl = "http://localhost:8080/";
            String accept = "notification/confirm/" + tokenString;
            String reject = "notification/reject/" + tokenString;

            String body = "Sei stato invitato al gruppo "  + dto.getName() + "\nPer accettare l'invito: " + baseURl+accept + "\nPer rifiutare: " + baseURl+reject;
            String email = "s" + sId +"@studenti.polito.it";
            System.out.println(email);
            this.sendMessage(email, "Group Invitation", body);

        }

    }


    @Scheduled(fixedRate = 1000)
    public void removeExpiredToken() {
       List<Token> tokens = tokenRepository.findAllByExpiryBefore(new Timestamp(Calendar.getInstance().getTimeInMillis()));
       tokens.forEach(t -> {
               teamService.evictTeam(t.getTeamId());
               tokenRepository.delete(t);

               });
    }
}
