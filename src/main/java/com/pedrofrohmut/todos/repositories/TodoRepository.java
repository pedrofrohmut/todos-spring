package com.pedrofrohmut.todos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.pedrofrohmut.todos.dtos.CreateTodoDto;

public class TodoRepository {

  private final Connection connection;

  public TodoRepository(Connection connection) {
    this.connection = connection;
  }

  public void create(CreateTodoDto dto) {
    try (
      final var stm = getPreparedStatementToCreate(dto);
    ) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToCreate(CreateTodoDto dto) throws SQLException {
    final var namePosition = 1;
    final var descriptionPosition = 2;
    final var isDonePosition = 3;
    final var taskIdPosition = 4;
    final var userIdPosition = 5;
    final var sql =
      "INSERT INTO app.todos (name, description, is_done, task_id, user_id) VALUES (?, ?, ?, ?, ?)";
    final var stm = this.connection.prepareStatement(sql);
    stm.setString(namePosition, dto.name);
    stm.setString(descriptionPosition, dto.description);
    stm.setBoolean(isDonePosition, dto.isDone);
    stm.setObject(taskIdPosition, java.util.UUID.fromString(dto.taskId));
    stm.setObject(userIdPosition, java.util.UUID.fromString(dto.userId));
    return stm;
  }

}
