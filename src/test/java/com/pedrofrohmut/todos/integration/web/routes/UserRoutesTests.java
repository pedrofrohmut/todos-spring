package com.pedrofrohmut.todos.integration.web.routes;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.util.Map;
import java.util.UUID;

import com.pedrofrohmut.todos.domain.dtos.SignInUserDto;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.infra.factories.ConnectionFactory;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.utils.dataaccess.UserDataAccessUtil;
import com.pedrofrohmut.todos.web.routes.UserRoutes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("User Routes")
public class UserRoutesTests {

  private final Connection connection;
  private final PasswordService passwordService;
  private final UserDataAccessUtil userDataAccessUtil;
  private final UserRoutes userRoutes;

  public UserRoutesTests() {
    this.connection = new ConnectionFactory().getConnection();
    this.passwordService = new BcryptPasswordService();
    this.userDataAccessUtil = new UserDataAccessUtil(connection);
    this.userRoutes = new UserRoutes();
  }

  @Test
  @DisplayName("Sign in with created user")
  void signIn() {
    userDataAccessUtil.deleteAllUsers();
    // Setup
    final var email = "user@mail.com";
    final var password = "123";
    final var credentials = new SignInUserDto(email, password);
    final var passwordHash = passwordService.hashPassword(password);
    final var newUser = new User(UUID.randomUUID().toString(), "User Name", email, passwordHash);
    userDataAccessUtil.create(newUser);
    final var foundUser = userDataAccessUtil.findByEmail(email);
    // Given
    assertThat(foundUser).isNotNull();
    // When
    final var response = userRoutes.signIn(credentials);
    // Then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

}
