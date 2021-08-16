package com.pedrofrohmut.todos.utils.dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pedrofrohmut.todos.domain.entities.User;

public class UserDataAccessUtil {

  private final Connection connection;

  public UserDataAccessUtil(Connection connection) {
    this.connection = connection;
  }

  public void create(User user) {
    final var sql = "INSERT INTO app.users (name, email, password_hash) VALUES (?, ?, ?)";
    try (final var stm = this.connection.prepareStatement(sql)) {
      stm.setString(1, user.getName());
      stm.setString(2, user.getEmail());
      stm.setString(3, user.getPasswordHash());
      stm.executeUpdate();
    } catch (SQLException e) {}
  }

  public void deleteAllUsers() {
    try (final var stm = this.connection.prepareStatement("DELETE FROM app.users")) {
      stm.executeUpdate();
    } catch (SQLException e) { System.out.println(e.getMessage()); }
  }

  public User findByEmail(String email) {
    PreparedStatement stm = null;
    ResultSet rs = null;
    try {
      stm = this.connection.prepareStatement("SELECT * FROM app.users WHERE email = ?");
      stm.setString(1, email);
      rs = stm.executeQuery();
      if (rs.next()) {
        return new User(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password_hash")
            );
      }
    } catch (SQLException e) {
    } finally {
      try { rs.close(); stm.close(); } catch (SQLException e) {}
    }
    return null;
  }

}
