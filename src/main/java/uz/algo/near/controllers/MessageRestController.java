package uz.algo.near.controllers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import uz.algo.near.components.ChatDateUtil;
import uz.algo.near.domains.Message;
import uz.algo.near.domains.User;
import uz.algo.near.models.MessageListItem;
import uz.algo.near.models.PostMessage;
import uz.algo.near.models.QueueMessageListItem;
import uz.algo.near.repositories.MessageRepository;
import uz.algo.near.security.SecurityUtil;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/message")
public class MessageRestController {

    private SecurityUtil securityUtil;
    private MessageRepository messageRepository;
    private ChatDateUtil chatDateUtil;
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;
    private SimpMessagingTemplate messagingTemplate;

    public MessageRestController(SecurityUtil securityUtil,
                                 MessageRepository messageRepository,
                                 ChatDateUtil chatDateUtil,
                                 JdbcTemplate jdbcTemplate,
                                 EntityManager entityManager,
                                 SimpMessagingTemplate messagingTemplate) {
        this.securityUtil = securityUtil;
        this.messageRepository = messageRepository;
        this.chatDateUtil = chatDateUtil;
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/user/{id}")
    private List<MessageListItem> getChatMessages(@PathVariable Long id,
                                                  @RequestParam(required = false, defaultValue = "0") int page) {

        jdbcTemplate.update("update message set seen = True where to_id = ? and from_id = ?", securityUtil.getCurrentUser().getId(), id);

        Long userId = securityUtil.getCurrentUser().getId();
        List<Message> messages = messageRepository.findAllUserMessages(userId, id, 20 * page);
        return messages
                .stream()
                .sorted(Comparator.comparing(Message::getMessageTime))
                .map(message -> {
                    MessageListItem item = new MessageListItem();
                    item.setText(message.getMessage());
                    item.setDate(chatDateUtil.transform(message.getMessageTime()));
                    item.setMine(message.getFrom().getId().equals(userId));
                    return item;
                }).collect(Collectors.toList());
    }

    @PostMapping("/post")
    private void postMessage(@RequestBody PostMessage postMessage) {
        User from = entityManager.getReference(User.class, securityUtil.getCurrentUser().getId());
        User to = entityManager.getReference(User.class, postMessage.getToId());
        Date now = new Date();
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        message.setMessage(postMessage.getMessage());
        message.setSeen(false);
        message.setMessageTime(now);
        messageRepository.save(message);

        QueueMessageListItem item = new QueueMessageListItem();
        item.setFromId(from.getId());
        item.setDate(chatDateUtil.transform(now));
        item.setMine(false);
        item.setText(postMessage.getMessage());
        messagingTemplate.convertAndSendToUser(to.getEmail(), "/topic/message", item);

    }

}
