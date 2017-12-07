package uz.algo.near.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import uz.algo.near.domains.User;
import uz.algo.near.models.TypingCommand;
import uz.algo.near.models.TypingUser;
import uz.algo.near.security.CustomAuthenticationToken;
import uz.algo.near.security.SecurityUtil;

import java.security.Principal;

@Controller
public class QueueBrokerController {

    private SimpMessagingTemplate messagingTemplate;
    private SecurityUtil securityUtil;

    public QueueBrokerController(SimpMessagingTemplate messagingTemplate, SecurityUtil securityUtil) {
        this.messagingTemplate = messagingTemplate;
        this.securityUtil = securityUtil;
    }

    @MessageMapping("/typing")
    public void typing(TypingUser typingUser, Principal principal) {
        CustomAuthenticationToken token = (CustomAuthenticationToken) principal;
        User currentUser = (User) token.getPrincipal();
        messagingTemplate.convertAndSendToUser(typingUser.getName(), "/topic/typing",
                new TypingCommand(currentUser.getId(), typingUser.isTyping()));
    }

}
