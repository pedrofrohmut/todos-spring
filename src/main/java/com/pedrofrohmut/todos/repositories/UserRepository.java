package com.pedrofrohmut.todos.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pedrofrohmut.todos.dtos.CreateUserDto;
import com.pedrofrohmut.todos.dtos.UserDto;

public class UserRepository {

  private final Connection connection;

  public UserRepository(Connection connection) {
    this.connection = connection;
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

  private UserDto mapResultToFindByEmail(String email, ResultSet rs) throws SQLException {
    final var foundUser = new UserDto();
    foundUser.id = rs.getString("id");
    foundUser.name = rs.getString("name");
    foundUser.email = email;
    foundUser.passwordHash = rs.getString("password_hash");
    return foundUser;
  }

  public UserDto findByEmail(String email) {
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

  private PreparedStatement getPreparedStatementToFindById(String userId) throws SQLException {
    final var idPosition = 1;
    final var sql = "SELECT name, email, password_hash FROM app.users WHERE id = ?";
    final var stm = this.connection.prepareStatement(sql);
    stm.setObject(idPosition, java.util.UUID.fromString(userId));
    return stm;
  }

  private ResultSet getResultSetToFindById(PreparedStatement stm) throws SQLException {
    final var resultSet = stm.executeQuery();
    return resultSet;
  }

  private UserDto mapResultToFindById(String userId, ResultSet rs) throws SQLException {
    final var foundUser = new UserDto();
    foundUser.id = userId;
    foundUser.name = rs.getString("name");
    foundUser.email = rs.getString("email");
    foundUser.passwordHash = rs.getString("password_hash");
    return foundUser;
  }

  public UserDto findById(String userId) {
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

  private PreparedStatement getPrepareStatementToCreate(CreateUserDto dto) throws SQLException {
    final var namePosition = 1;
    final var emailPosition = 2;
    final var passwordHashPosition = 3;
    final var sql = "INSERT INTO app.users (name, email, password_hash) VALUES (?, ?, ?)";
    final var preparedStatement = this.connection.prepareStatement(sql);
    preparedStatement.setString(namePosition, dto.name);
    preparedStatement.setString(emailPosition, dto.email);
    preparedStatement.setString(passwordHashPosition, dto.passwordHash);
    return preparedStatement;
  }

  public void create(CreateUserDto dto) {
    try (final var stm = getPrepareStatementToCreate(dto)) {
      stm.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
