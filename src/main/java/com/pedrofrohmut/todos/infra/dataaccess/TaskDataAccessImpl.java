package com.pedrofrohmut.todos.infra.dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.entities.Task;

public class TaskDataAccessImpl implements TaskDataAccess {

  private final Connection connection;

  public TaskDataAccessImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void create(Task newTask) {
    try (final var stm = getPreparedStatementToCreate(newTask)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToCreate(Task newTask) throws SQLException {
    final var namePosition = 1;
    final var descriptionPosition = 2;
    final var userIdPosition = 3;
    final var sql = "INSERT INTO app.tasks (name, description, user_id) VALUES (?, ?, ?)";
    final var stm = this.connection.prepareStatement(sql);
    stm.setString(namePosition, newTask.getName());
    stm.setString(descriptionPosition, newTask.getDescription());
    stm.setObject(userIdPosition, UUID.fromString(newTask.getUserId()));
    return stm;
  }

  @Override
  public Task findById(String taskId) {
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

  private PreparedStatement getPreparedStatementToFindById(String taskId) throws SQLException {
    final var taskIdPosition = 1;
    final var sql = "SELECT name, description, user_id FROM app.tasks WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(taskIdPosition, UUID.fromString(taskId));
    return stm;
  }

  private ResultSet getResultSetToFindById(PreparedStatement stm) throws SQLException {
    final var rs = stm.executeQuery();
    return rs;
  }

  private Task mapResultToFindById(String taskId, ResultSet rs) throws SQLException {
    final var taskName = rs.getString("name");
    final var taskDescription =
      rs.getString("description") == null ? "" : rs.getString("description");
    final var userId = rs.getString("user_id");
    final var foundTask = new Task(taskId, taskName, taskDescription, userId);
    return foundTask;
  }

  @Override
  public List<Task> findByUserId(String userId) {
    try (
      final var stm = getPreparedStatementToFindByUserId(userId);
      final var rs = getResultSetToFindByUserId(stm);
    ) {
      final var hasResults = rs.next();
      if (!hasResults) {
        return new ArrayList<Task>();
      }
      final var foundTasks = mapResultToFindByUserId(userId, rs);
      return foundTasks;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  private PreparedStatement getPreparedStatementToFindByUserId(String userId) throws SQLException {
    final var userIdPosition = 1;
    final var sql = "SELECT id, name, description FROM app.tasks WHERE user_id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(userIdPosition, UUID.fromString(userId));
    return stm;
  }

  private ResultSet getResultSetToFindByUserId(PreparedStatement stm) throws SQLException {
    final var rs = stm.executeQuery();
    return rs;
  }

  private List<Task> mapResultToFindByUserId(String userId, ResultSet rs) throws SQLException {
    final var tasks = new ArrayList<Task>();
    do {
      final var taskId = rs.getString("id");
      final var taskName = rs.getString("name");
      final var taskDescription =
        rs.getString("description") == null ? "" : rs.getString("description");
      final var task = new Task(taskId, taskName, taskDescription, userId);
      tasks.add(task);
    } while (rs.next());
    return tasks;
  }

  @Override
  public void update(Task updatedTask) {
    try (
      final var stm = getPreparedStatementToUpdate(updatedTask);
    ) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToUpdate(Task updatedTask)
      throws SQLException {
    final var namePosition = 1;
    final var descriptionPosition = 2;
    final var taskIdPosition = 3;
    final var sql = "UPDATE app.tasks SET name = ?, description = ? WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setString(namePosition, updatedTask.getName());
    stm.setString(descriptionPosition, updatedTask.getDescription());
    stm.setObject(taskIdPosition, UUID.fromString(updatedTask.getId()));
    return stm;
  }

  @Override
  public void delete(String taskId) {
    try (
      final var stm = getPreparedStatmentToDelete(taskId);
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
    stm.setObject(taskIdPosition, UUID.fromString(taskId));
    return stm;
  }

}
