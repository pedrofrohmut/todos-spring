package com.pedrofrohmut.todos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.pedrofrohmut.todos.dtos.CreateTaskDto;

public class TaskRepository {

  private final Connection connection;

  public TaskRepository(Connection connection) {
    this.connection = connection;
  }

  private PreparedStatement getPrepareStatementToCreate(CreateTaskDto dto) throws SQLException {
    final var namePosition = 1;
    final var descriptionPosition = 2;
    final var userIdPosition = 3;
    final var sql = "INSERT INTO app.tasks (name, description, user_id) VALUES (?, ?, ?)";
    final var stm = this.connection.prepareStatement(sql);
    stm.setString(namePosition, dto.name);
    stm.setString(descriptionPosition, dto.description);
    stm.setObject(userIdPosition, java.util.UUID.fromString(dto.userId));
    return stm;
  }

  public void create(CreateTaskDto dto) {
    try (final var stm = getPrepareStatementToCreate(dto)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
