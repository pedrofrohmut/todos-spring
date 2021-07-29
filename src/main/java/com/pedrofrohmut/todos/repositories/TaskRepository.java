package com.pedrofrohmut.todos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.pedrofrohmut.todos.dtos.CreateTaskDto;
import com.pedrofrohmut.todos.dtos.TaskDto;
import com.pedrofrohmut.todos.dtos.UpdateTaskDto;

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

  private TaskDto mapResultToFindById(String taskId, ResultSet rs) throws SQLException {
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
      final var foundTask = mapResultToFindById(taskId, rs);
      return foundTask;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToFindByUserId(String userId) throws SQLException {
    final var userIdPosition = 1;
    final var sql = "SELECT id, name, description FROM app.tasks WHERE user_id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(userIdPosition, java.util.UUID.fromString(userId));
    return stm;
  }

  private ResultSet getResultSetToFindByUserId(PreparedStatement stm) throws SQLException {
    final var rs = stm.executeQuery();
    return rs;
  }

  private List<TaskDto> mapResultToFindByUserId(String userId, ResultSet rs) throws SQLException {
    final var tasks = new ArrayList<TaskDto>();
    do {
      final var task = new TaskDto();
      task.id = rs.getString("id");
      task.name = rs.getString("name");
      task.description = rs.getString("description") == null ? "" : rs.getString("description");
      task.userId = userId;
      tasks.add(task);
    } while (rs.next());
    return tasks;
  }

  public List<TaskDto> findByUserId(String userId) {
    try (
      final var stm = getPreparedStatementToFindByUserId(userId);
      final var rs = getResultSetToFindByUserId(stm);
    ) {
      final var hasResults = rs.next();
      if (!hasResults) {
        return new ArrayList<TaskDto>();
      }
      final var foundTasks = mapResultToFindByUserId(userId, rs);
      return foundTasks;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToUpdate(String taskId, UpdateTaskDto dto)
      throws SQLException {
    final var namePosition = 1;
    final var descriptionPosition = 2;
    final var taskIdPosition = 3;
    final var sql = "UPDATE app.tasks SET name = ?, description = ? WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setString(namePosition, dto.name);
    stm.setString(descriptionPosition, dto.description);
    stm.setObject(taskIdPosition, java.util.UUID.fromString(taskId));
    return stm;
  }

  public void update(String taskId, UpdateTaskDto dto) {
    try (
      final var stm = getPreparedStatementToUpdate(taskId, dto);
    ) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatmentToDelete(String taskId) throws SQLException {
    final var taskIdPosition = 1;
    final var sql = "DELETE FROM app.tasks WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(taskIdPosition, java.util.UUID.fromString(taskId));
    return stm;
  }

  public void delete(String taskId) {
    try (
      final var stm = getPreparedStatmentToDelete(taskId);
    ) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
