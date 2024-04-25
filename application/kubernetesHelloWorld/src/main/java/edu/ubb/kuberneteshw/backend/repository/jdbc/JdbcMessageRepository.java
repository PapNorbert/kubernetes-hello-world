package edu.ubb.kuberneteshw.backend.repository.jdbc;

import edu.ubb.kuberneteshw.backend.model.Message;
import edu.ubb.kuberneteshw.backend.repository.MessageRepository;
import edu.ubb.kuberneteshw.backend.repository.exception.RepositoryNotFoundException;
import edu.ubb.kuberneteshw.backend.repository.exception.RepositoryException;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class JdbcMessageRepository implements MessageRepository {
    private final ConnectionManager connectionManager;

    public JdbcMessageRepository() {
        this.connectionManager = ConnectionManager.getInstance();
    }

    @Override
    public Message save(Message message) {
        String query = "INSERT INTO messages "
                + " (id, message) VALUES"
                + " (default, ?)";
        try (Connection connection = ConnectionManager.getInstance().getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, message.getMessage());

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                message.setId(keys.getLong(1));
            }
            return message;
        } catch (SQLException e) {
            throw new RepositoryException("Error inserting message", e);
        }

    }

    @Override
    public Message findById(Long id) {
        String query = "SELECT id, message FROM messages "
                + "WHERE id = ?";
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setLong(1, id);
            ResultSet result = stmt.executeQuery();
            if (result.next()) {
                Message message = new Message();
                message.setId(result.getLong("id"));
                message.setMessage(result.getString("message"));
                return message;
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryNotFoundException("Failed to get message with id" + id, e);
        }
    }

    @Override
    public Collection<Message> findAll() {
        String query = "SELECT id, message FROM messages ";
        try (Connection connection = connectionManager.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet result = stmt.executeQuery();

            List<Message> messages = new ArrayList<>();
            while (result.next()) {
                Message message = new Message();
                message.setId(result.getLong("id"));
                message.setMessage(result.getString("message"));
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            throw new RepositoryNotFoundException("Failed to get all messages", e);
        }
    }


}
