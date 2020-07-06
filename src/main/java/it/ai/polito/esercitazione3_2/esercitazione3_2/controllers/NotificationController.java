package it.ai.polito.esercitazione3_2.esercitazione3_2.controllers;


import it.ai.polito.esercitazione3_2.esercitazione3_2.exceptions.ConfirmationException;
import it.ai.polito.esercitazione3_2.esercitazione3_2.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    NotificationService ns;

    @GetMapping(value="/confirm/{token}")
    public String acceptingGroup(@PathVariable String token){
        try {
           if(ns.confirm(token)) return "confirm";

           return "error";
        }
        catch(ConfirmationException e){
            return "waiting";
        }


    }

    @GetMapping(value="/reject/{token}")
    public String rejectingGroup(@PathVariable String token){
         if(ns.reject(token)) return "reject";

         return "error";
    }

}
