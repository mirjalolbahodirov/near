package uz.algo.near.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import uz.algo.near.domains.User;
import uz.algo.near.repositories.UserRepository;
import uz.algo.near.security.CustomAuthenticationToken;

import java.security.Principal;

@Component
public class SocketSessionListener {

    private UserRepository userRepository;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SocketSessionListener(UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void connectListener(SessionConnectedEvent event) {
        changeUserStatus(event, true);
        messagingTemplate.convertAndSend("/topic/user-list-change", "test");
    }

    @EventListener
    public void disconnectListener(SessionDisconnectEvent event) {
        changeUserStatus(event, false);
        messagingTemplate.convertAndSend("/topic/user-list-change", "test");
    }

    private User getSessionUser(Principal principal) {
        return (User) ((CustomAuthenticationToken) principal).getPrincipal();
    }

    private void changeUserStatus(AbstractSubProtocolEvent event, boolean active) {
        User user = userRepository.findOne(getSessionUser(event.getUser()).getId());
        user.setActive(active);
        userRepository.save(user);
    }

}
