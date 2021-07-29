package com.pedrofrohmut.todos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pedrofrohmut.todos.dtos.CreateTodoDto;
import com.pedrofrohmut.todos.dtos.TodoDto;

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

  private PreparedStatement getPreparedStatementToFindById(String todoId) throws SQLException {
    final var todoIdPosition = 1;
    final var sql = "SELECT name, description, is_done, task_id, user_id FROM app.todos WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(todoIdPosition, java.util.UUID.fromString(todoId));
    return stm;
  }

  private ResultSet getResultSetToFindById(PreparedStatement stm) throws SQLException {
    final var rs = stm.executeQuery();
    return rs;
  }

  private TodoDto mapResultToFindById(String todoId, ResultSet rs) throws SQLException {
    final var dto = new TodoDto();
    dto.id = todoId;
    dto.name = rs.getString("name");
    dto.description = rs.getString("description");
    dto.isDone = rs.getBoolean("is_done");
    dto.taskId = rs.getString("task_id");
    dto.userId = rs.getString("user_id");
    return dto;
  }

  public TodoDto findById(String todoId) {
    try (
      final var stm = getPreparedStatementToFindById(todoId);
      final var rs = getResultSetToFindById(stm);
    ) {
      final var hasResults = rs.next();
      if (!hasResults) {
        return null;
      }
      final var todo = mapResultToFindById(todoId, rs);
      return todo;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
