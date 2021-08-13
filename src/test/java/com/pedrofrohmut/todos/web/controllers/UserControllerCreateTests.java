package com.pedrofrohmut.todos.web.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.CreateUserDto;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.errors.InvalidUserException;
import com.pedrofrohmut.todos.domain.errors.UserEmailAlreadyTakenException;
import com.pedrofrohmut.todos.domain.services.JwtService;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.UserUseCase;
import com.pedrofrohmut.todos.infra.dataaccess.UserDataAccessImpl;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.infra.services.JjwtJwtService;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User controller - create method")
class UserControllerCreateTests {

  final PasswordService passwordService = new BcryptPasswordService();
  final JwtService jwtService = new JjwtJwtService();
  final UserDataAccess userDataAccess = mock(UserDataAccessImpl.class);
  final UserUseCase userUseCase = new UserUseCase(userDataAccess, passwordService, jwtService);
  final UserController controller = new UserController(userUseCase);

  AdaptedRequest<CreateUserDto> request;

  @BeforeEach
  void beforeEach() {
    request = new AdaptedRequest<>(null, null, null);
  }

  @Test
  @DisplayName("Null body -> 400/message")
  void nullBody() {
    // Given
    assertThat(request.body).isNull();
    // When
    final var controllerResponse = controller.create(request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestBodyException.message);
  }

  @Test
  @DisplayName("Invalid body.name => 400/message")
  void invalidBodyName() {
    final var invalidName = "a";
    Exception nameErr = getNameErr(invalidName);
    request.body = new CreateUserDto(invalidName, "user@mail.com", "user_password");
    // Given
    assertThat(nameErr).isNotNull().isInstanceOf(InvalidUserException.class);
    assertThat(request.body.name).isEqualTo(invalidName);
    // When
    final var controllerResponse = controller.create(request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(nameErr.getMessage());
  }

  private Exception getNameErr(final String invalidName) {
    Exception nameErr = null;
    try {
      User.validateName(invalidName);
    } catch (Exception e) {
      nameErr = e;
    }
    return nameErr;
  }

  @Test
  @DisplayName("Invalid body.email => 400/message")
  void invalidBodyEmail() {
    final var invalidEmail = "mail";
    Exception emailErr = getEmailErr(invalidEmail);
    request.body = new CreateUserDto("John Doe", invalidEmail, "password123");
    // Given
    assertThat(emailErr).isNotNull().isInstanceOf(InvalidUserException.class);
    assertThat(request.body.email).isEqualTo(invalidEmail);
    // When
    final var controllerResponse = controller.create(request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(emailErr.getMessage());
  }

  private Exception getEmailErr(final String invalidEmail) {
    Exception emailErr = null;
    try {
      User.validateEmail(invalidEmail);
    } catch (Exception e) {
      emailErr = e;
    }
    return emailErr;
  }

  @Test
  @DisplayName("Invalid body.password => 400/message")
  void invalidBodyPassword() {
    final var invalidPassword = "";
    Exception passwordErr = getPasswordErr(invalidPassword);
    request.body = new CreateUserDto("User Name", "user@mail.com", invalidPassword);
    // Given
    assertThat(passwordErr).isNotNull().isInstanceOf(InvalidUserException.class);
    assertThat(request.body.password).isEqualTo(invalidPassword);
    // When
    final var controllerResponse = controller.create(request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(passwordErr.getMessage());
  }

  private Exception getPasswordErr(final String invalidPassword) {
    Exception passwordErr = null;
    try {
      User.validatePassword(invalidPassword);
    } catch (Exception e) {
      passwordErr = e;
    }
    return passwordErr;
  }

  @Test
  @DisplayName("Valid request, but user email already registered => 400/message")
  void emailAlreadyRegistered() {
    final var newUser = new CreateUserDto("User Name", "user@email.com", "user_password");
    final var mockUserDataAccess = mock(UserDataAccessImpl.class);
    final var controller = getControllerMockReturnUserDB(newUser, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findByEmail(newUser.email);
    request.body = newUser;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(request.body).isEqualTo(newUser);
    // When
    final var controllerResponse = controller.create(request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserEmailAlreadyTakenException.message);
  }

  private UserController getControllerMockReturnUserDB(final CreateUserDto newUser, final UserDataAccess mockUserDataAccess) {
    final var passwordHash = passwordService.hashPassword(newUser.password);
    final var userDB = new User(UUID.randomUUID().toString(), newUser.name, newUser.email, passwordHash);
    when(mockUserDataAccess.findByEmail(newUser.email)).thenReturn(userDB);
    final var userUseCase = new UserUseCase(mockUserDataAccess, passwordService, jwtService);
    final var controller = new UserController(userUseCase);
    return controller;
  }

  @Test
  @DisplayName("Valid request and email is not registered => 201")
  void emailNotRegistered() {
    final var newUser = new CreateUserDto("User Name", "user@email.com", "user_password");
    final var mockUserDataAccess = mock(UserDataAccessImpl.class);
    final var controller = getControllerMockReturnNull(newUser, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findByEmail(newUser.email);
    request.body = newUser;
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.body).isEqualTo(newUser);
    // When
    final var controllerResponse = controller.create(request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(201);
  }

  private UserController getControllerMockReturnNull(final CreateUserDto newUser, final UserDataAccess mockUserDataAccess) {
    when(mockUserDataAccess.findByEmail(newUser.email)).thenReturn(null);
    final var userUseCase = new UserUseCase(mockUserDataAccess, passwordService, jwtService);
    final var controller = new UserController(userUseCase);
    return controller;
  }

}
