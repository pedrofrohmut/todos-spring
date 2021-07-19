package com.pedrofrohmut.todos.repositories;

import java.sql.Connection;
import java.sql.SQLException;

import com.pedrofrohmut.todos.dtos.CreateUserDto;
import com.pedrofrohmut.todos.dtos.UserDto;

public class UserRepository {

  private final Connection connection;

  public UserRepository(Connection connection) {
    this.connection = connection;
  }

  public UserDto findByEmail(String email) throws SQLException {
    final var sql = "SELECT id, name, password_hash FROM app.users WHERE email = ?";
    final var preparedStatement = this.connection.prepareStatement(sql);
    preparedStatement.setString(1, email);
    final var resultSet = preparedStatement.executeQuery();
    final var hasResults = resultSet.next();
    final var foundUser = new UserDto();
    if (hasResults) {
      foundUser.id = resultSet.getString("id");
      foundUser.name = resultSet.getString("name");
      foundUser.email = email;
      foundUser.passwordHash = resultSet.getString("password_hash");
    }
    preparedStatement.close();
    resultSet.close();
    if (!hasResults) {
      return null;
    }
    return foundUser;
  }

  public void create(CreateUserDto dto) throws SQLException {
    final var stm = this.connection.prepareStatement("INSERT INTO app.users (name, email, password_hash) VALUES (?, ?, ?)");
    stm.setString(1, dto.name);
    stm.setString(2, dto.email);
    stm.setString(3, dto.passwordHash);
    stm.executeUpdate();
    stm.close();
  }

}
