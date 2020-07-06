package it.ai.polito.esercitazione3_2.esercitazione3_2.services;



import it.ai.polito.esercitazione3_2.esercitazione3_2.dtos.TeamDTO;

import java.util.List;

public interface NotificationService {
    void sendMessage(String address, String subject, String body);
    boolean confirm(String token);
    boolean reject(String token);
    void notifyTeam(TeamDTO dto, List<String> memberIds);
}
