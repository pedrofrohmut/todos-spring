package com.pedrofrohmut.todos.infra.dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.TodoDataAccess;
import com.pedrofrohmut.todos.domain.entities.Todo;

public class TodoDataAccessImpl implements TodoDataAccess {

  private final Connection connection;

  public TodoDataAccessImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void create(Todo newTodo) {
    try (final var stm = getPreparedStatementToCreate(newTodo)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToCreate(Todo newTodo) throws SQLException {
    final var namePosition = 1;
    final var descriptionPosition = 2;
    final var isDonePosition = 3;
    final var taskIdPosition = 4;
    final var userIdPosition = 5;
    final var sql =
      "INSERT INTO app.todos (name, description, is_done, task_id, user_id) VALUES (?, ?, ?, ?, ?)";
    final var stm = this.connection.prepareStatement(sql);
    stm.setString(namePosition, newTodo.getTitle());
    stm.setString(descriptionPosition, newTodo.getDescription());
    stm.setBoolean(isDonePosition, newTodo.isDone());
    stm.setObject(taskIdPosition, UUID.fromString(newTodo.getTaskId()));
    stm.setObject(userIdPosition, UUID.fromString(newTodo.getUserId()));
    return stm;
  }

  @Override
  public Todo findById(String todoId) {
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
    stm.setObject(todoIdPosition, UUID.fromString(todoId));
    return stm;
  }

  private ResultSet getResultSetToFindById(PreparedStatement stm) throws SQLException {
    final var rs = stm.executeQuery();
    return rs;
  }

  private Todo mapResultToFindById(String todoId, ResultSet rs) throws SQLException {
    final var todoTitle = rs.getString("name");
    final var todoDescription = rs.getString("description");
    final var todoIsDone = rs.getBoolean("is_done");
    final var taskId = rs.getString("task_id");
    final var userId = rs.getString("user_id");
    final var todo = new Todo(todoId, todoTitle, todoDescription, todoIsDone, taskId, userId);
    return todo;
  }

  @Override
  public List<Todo> findByTaskId(String taskId) {
    try (
      final var stm = getPreparedStatementToFindByTaskId(taskId);
      final var rs = getResultSetToFindByTaskId(stm);
    ) {
      final var hasResults = rs.next();
      if (!hasResults) {
        return new ArrayList<Todo>();
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
    stm.setObject(taskIdPosition, UUID.fromString(taskId));
    return stm;
  }

  private ResultSet getResultSetToFindByTaskId(PreparedStatement stm) throws SQLException {
    final var rs = stm.executeQuery();
    return rs;
  }

  private List<Todo> mapResultToFindByTaskById(String taskId, ResultSet rs) throws SQLException {
    final var todos = new ArrayList<Todo>();
    do {
      final var todoId = rs.getString("id");
      final var todoTitle = rs.getString("name");
      final var todoDescription = rs.getString("description");
      final var todoIsDone = rs.getBoolean("is_done");
      final var userId = rs.getString("user_id");
      final var todo = new Todo(todoId, todoTitle, todoDescription, todoIsDone, taskId, userId);
      todos.add(todo);
    } while (rs.next());
    return todos;
  }

  @Override
  public void update(Todo updatedTodo) {
    try (final var stm = getPreparedStatementToUpdate(updatedTodo)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToUpdate(Todo updatedTodo) throws SQLException {
    final var namePosition = 1;
    final var descriptionPosition = 2;
    final var todoIdPosition = 3;
    final var sql = "UPDATE app.todos SET name = ?, description = ? WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setString(namePosition, updatedTodo.getTitle());
    stm.setString(descriptionPosition, updatedTodo.getDescription());
    stm.setObject(todoIdPosition, UUID.fromString(updatedTodo.getId()));
    return stm;
  }

  @Override
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
    stm.setObject(todoIdPosition, UUID.fromString(todoId));
    return stm;
  }

  @Override
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
    stm.setObject(todoIdPosition, UUID.fromString(todoId));
    return stm;
  }

  @Override
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
    stm.setObject(todoIdPosition, UUID.fromString(todoId));
    return stm;
  }

  @Override
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
    stm.setObject(taskIdPosition, UUID.fromString(taskId));
    return stm;
  }

}
