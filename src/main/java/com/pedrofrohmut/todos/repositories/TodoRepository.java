package com.pedrofrohmut.todos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.pedrofrohmut.todos.dtos.CreateTodoDto;
import com.pedrofrohmut.todos.dtos.TodoDto;
import com.pedrofrohmut.todos.dtos.UpdateTodoDto;

public class TodoRepository {

  private final Connection connection;

  public TodoRepository(Connection connection) {
    this.connection = connection;
  }

  public void create(CreateTodoDto dto) {
    try (final var stm = getPreparedStatementToCreate(dto)) {
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

  public List<TodoDto> findByTaskId(String taskId) {
    try (
      final var stm = getPreparedStatementToFindByTaskId(taskId);
      final var rs = getResultSetToFindByTaskId(stm);
    ) {
      final var hasResults = rs.next();
      if (!hasResults) {
        return new ArrayList<TodoDto>();
      }
      final var todos = mapResultToFindByTaskById(taskId, rs);
      return todos;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToFindByTaskId(String taskId) throws SQLException {
    final var taskIdPosition = 1;
    final var sql = "SELECT id, name, description, is_done, user_id FROM app.todos WHERE task_id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(taskIdPosition, java.util.UUID.fromString(taskId));
    return stm;
  }

  private ResultSet getResultSetToFindByTaskId(PreparedStatement stm) throws SQLException {
    final var rs = stm.executeQuery();
    return rs;
  }

  private List<TodoDto> mapResultToFindByTaskById(String taskId, ResultSet rs) throws SQLException {
    final var todos = new ArrayList<TodoDto>();
    do {
      final var dto = new TodoDto();
      dto.id = rs.getString("id");
      dto.name = rs.getString("name");
      dto.description = rs.getString("description");
      dto.isDone = rs.getBoolean("is_done");
      dto.taskId = taskId;
      dto.userId = rs.getString("user_id");
      todos.add(dto);
    } while (rs.next());
    return todos;
  }

  private PreparedStatement getPreparedStatementToUpdate(UpdateTodoDto dto) throws SQLException {
    final var namePosition = 1;
    final var descriptionPosition = 2;
    final var todoIdPosition = 3;
    final var sql = "UPDATE app.todos SET name = ?, description = ? WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setString(namePosition, dto.name);
    stm.setString(descriptionPosition, dto.description);
    stm.setObject(todoIdPosition, java.util.UUID.fromString(dto.id));
    return stm;
  }

  public void update(UpdateTodoDto dto) {
    try (final var stm = getPreparedStatementToUpdate(dto)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void setDone(String todoId) {
    try (final var stm = getPreparedStatementToSetDone(todoId)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToSetDone(String todoId) throws SQLException {
    final var todoIdPosition = 1;
    final var sql = "UPDATE app.todos SET is_done = true WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(todoIdPosition, java.util.UUID.fromString(todoId));
    return stm;
  }

  public void setNotDone(String todoId) {
    try (final var stm = getPreparedStatementToSetNotDone(todoId)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToSetNotDone(String todoId) throws SQLException {
    final var todoIdPosition = 1;
    final var sql = "UPDATE app.todos SET is_done = false WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(todoIdPosition, java.util.UUID.fromString(todoId));
    return stm;
  }

  public void delete(String todoId) {
    try (final var stm = getPreparedStatementToDelete(todoId)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToDelete(String todoId) throws SQLException {
    final var todoIdPosition = 1;
    final var sql = "DELETE FROM app.todos WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(todoIdPosition, java.util.UUID.fromString(todoId));
    return stm;
  }

  public void clearCompleteByTaskId(String taskId) {
    try (final var stm = getPreparedStatementToClearCompleteByTaskId(taskId)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToClearCompleteByTaskId(String taskId)
      throws SQLException {
    final var taskIdPosition = 1;
    final var sql = "DELETE FROM app.todos WHERE task_id = ? AND is_done = true";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(taskIdPosition, java.util.UUID.fromString(taskId));
    return stm;
  }

}
