package com.pedrofrohmut.todos.infra.dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.entities.User;

public class UserDataAccessImpl implements UserDataAccess {

  private final Connection connection;

  public UserDataAccessImpl(Connection connection) {
    this.connection = connection;
  }

  @Override
  public User findByEmail(String email) {
    try (
      final var stm = getPreparedStatementToFindByEmail(email);
      final var rs = getResultSetToFindByEmail(stm);
    ) {
      final var hasResults = rs.next();
      if (!hasResults) {
        return null;
      }
      final var foundUser = mapResultToFindByEmail(email, rs);
      return foundUser;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToFindByEmail(String email) throws SQLException {
    final var emailPosition = 1;
    final var sql = "SELECT id, name, password_hash FROM app.users WHERE email = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setString(emailPosition, email);
    return stm;
  }

  private ResultSet getResultSetToFindByEmail(PreparedStatement stm) throws SQLException {
    final var resultSet = stm.executeQuery();
    return resultSet;
  }

  private User mapResultToFindByEmail(String email, ResultSet rs) throws SQLException {
    final var userId = rs.getString("id");
    final var userName = rs.getString("name");
    final var userPasswordHash = rs.getString("password_hash");
    final var foundUser = new User(userId, userName, email, userPasswordHash);
    return foundUser;
  }

  @Override
  public User findById(String userId) {
    try (
      final var stm = getPreparedStatementToFindById(userId);
      final var rs = getResultSetToFindById(stm);
    ) {
      final var hasResults = rs.next();
      if (!hasResults) {
        return null;
      }
      final var foundUser = mapResultToFindById(userId, rs);
      return foundUser;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPreparedStatementToFindById(String userId) throws SQLException {
    final var idPosition = 1;
    final var sql = "SELECT name, email, password_hash FROM app.users WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(idPosition, UUID.fromString(userId));
    return stm;
  }

  private ResultSet getResultSetToFindById(PreparedStatement stm) throws SQLException {
    final var resultSet = stm.executeQuery();
    return resultSet;
  }

  private User mapResultToFindById(String userId, ResultSet rs) throws SQLException {
    final var name = rs.getString("name");
    final var email = rs.getString("email");
    final var passwordHash = rs.getString("password_hash");
    final var foundUser = new User(userId, name, email, passwordHash);
    return foundUser;
  }

  @Override
  public void create(User newUser) {
    try (final var stm = getPrepareStatementToCreate(newUser)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private PreparedStatement getPrepareStatementToCreate(User newUser) throws SQLException {
    final var namePosition = 1;
    final var emailPosition = 2;
    final var passwordHashPosition = 3;
    final var sql = "INSERT INTO app.users (name, email, password_hash) VALUES (?, ?, ?)";
    final var stm = this.connection.prepareStatement(sql);
    stm.setString(namePosition, newUser.getName());
    stm.setString(emailPosition, newUser.getEmail());
    stm.setString(passwordHashPosition, newUser.getPasswordHash());
    return stm;
  }

}
