package edu.ubb.kuberneteshw.backend.repository;

import edu.ubb.kuberneteshw.backend.model.Message;

import java.util.Collection;

public interface MessageRepository {
    Message findById(Long id);
    Collection<Message> findAll();
    Message save(Message message);
}
