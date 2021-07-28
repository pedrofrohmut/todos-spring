package com.pedrofrohmut.todos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pedrofrohmut.todos.dtos.CreateTaskDto;
import com.pedrofrohmut.todos.dtos.TaskDto;

public class TaskRepository {

  private final Connection connection;

  public TaskRepository(Connection connection) {
    this.connection = connection;
  }

  private PreparedStatement getPreparedStatementToCreate(CreateTaskDto dto) throws SQLException {
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
    try (final var stm = getPreparedStatementToCreate(dto)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToFindById(String taskId) throws SQLException {
    final var taskIdPosition = 1;
    final var sql = "SELECT name, description, user_id FROM app.tasks WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(taskIdPosition, java.util.UUID.fromString(taskId));
    return stm;
  }

  private ResultSet getResultSetToFindById(PreparedStatement stm) throws SQLException {
    final var rs = stm.executeQuery();
    return rs;
  }

  private TaskDto mapResulToFindById(String taskId, ResultSet rs) throws SQLException {
    final var dto = new TaskDto();
    dto.id = taskId;
    dto.name = rs.getString("name");
    dto.description = rs.getString("description") == null ? "" : rs.getString("description");
    dto.userId = rs.getString("user_id");
    return dto;
  }

  public TaskDto findById(String taskId) {
    try (
      final var stm = getPreparedStatementToFindById(taskId);
      final var rs = getResultSetToFindById(stm);
    ) {
      final var hasResults = rs.next();
      if (!hasResults) {
        return null;
      }
      final var foundTask = mapResulToFindById(taskId, rs);
      return foundTask;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
