package edu.ubb.kuberneteshw.backend.controller;

import edu.ubb.kuberneteshw.backend.controller.exception.ControllerNotFoundException;
import edu.ubb.kuberneteshw.backend.model.Message;
import edu.ubb.kuberneteshw.backend.repository.MessageRepository;
import edu.ubb.kuberneteshw.backend.repository.exception.RepositoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/db/messages")
public class MessageController {
    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    @GetMapping()
    public Collection<Message> findAll() {
        System.out.println("Get request at /db/messages/");
        return messageRepository.findAll();
    }

    @GetMapping("/{id}")
    public Message findById(@PathVariable("id") Long id) {
        System.out.println("Get request at /db/messages/" + id);
        try {
            Message message = messageRepository.findById(id);
            if (message == null) {
                throw new ControllerNotFoundException();
            } else {
                return message;
            }
        } catch (RepositoryNotFoundException e) {
            throw new ControllerNotFoundException();
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody String messageText) {
        System.out.println("Post request at /db/messages, message text: " + messageText);
        Message newMessage = new Message();
        newMessage.setMessage(messageText);
        return messageRepository.save(newMessage).getId();
    }


}
