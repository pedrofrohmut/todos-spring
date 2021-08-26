package com.pedrofrohmut.todos.unit.web.controllers.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.CreateUserDto;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.errors.InvalidUserException;
import com.pedrofrohmut.todos.domain.errors.UserEmailAlreadyTakenException;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.users.CreateUserUseCase;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.mocks.UserDataAccessMock;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.controllers.UserController;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("User controller - create method")
class UserControllerCreateTests {

  static final String USER_NAME = "User Name";
  static final String USER_EMAIL = "user@mail.com";
  static final String USER_PASSWORD = "user_password";

  final CreateUserUseCase createUserUseCase;
  final UserController userController;
  final PasswordService passwordService;


  public UserControllerCreateTests() {
    passwordService = new BcryptPasswordService();
    final var mockUserDataAccess = mock(UserDataAccess.class);
    createUserUseCase = new CreateUserUseCase(mockUserDataAccess, passwordService);
    userController = new UserController();
  }

  AdaptedRequest<CreateUserDto> request;

  @BeforeEach
  void beforeEach() {
    request = new AdaptedRequest<>(null, null, null);
  }

  @Test
  @DisplayName("Null body => 400/message")
  void nullBody() {
    // Given
    assertThat(request.body).isNull();
    // When
    final var controllerResponse = userController.create(createUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestBodyException.message);
  }

  @Test
  @DisplayName("Invalid body.name => 400/message")
  void invalidBodyName() {
    final var invalidName = "a";
    Exception nameErr = getNameErr(invalidName);
    request.body = new CreateUserDto(invalidName, USER_EMAIL, USER_PASSWORD);
    // Given
    assertThat(nameErr).isNotNull().isInstanceOf(InvalidUserException.class);
    assertThat(request.body.name).isEqualTo(invalidName);
    // When
    final var controllerResponse = userController.create(createUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(nameErr.getMessage());
  }

  private Exception getNameErr(String name) {
    try {
      User.validateName(name);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Invalid body.email => 400/message")
  void invalidBodyEmail() {
    final var invalidEmail = "invalid_mail";
    Exception emailErr = getEmailErr(invalidEmail);
    request.body = new CreateUserDto(USER_NAME, invalidEmail, USER_PASSWORD);
    // Given
    assertThat(emailErr).isNotNull().isInstanceOf(InvalidUserException.class);
    assertThat(request.body.email).isEqualTo(invalidEmail);
    // When
    final var controllerResponse = userController.create(createUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(emailErr.getMessage());
  }

  private Exception getEmailErr(String invalidEmail) {
    try {
      User.validateEmail(invalidEmail);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Invalid body.password => 400/message")
  void invalidBodyPassword() {
    final var invalidPassword = "";
    Exception passwordErr = getPasswordErr(invalidPassword);
    request.body = new CreateUserDto(USER_NAME, USER_EMAIL, invalidPassword);
    // Given
    assertThat(passwordErr).isNotNull().isInstanceOf(InvalidUserException.class);
    assertThat(request.body.password).isEqualTo(invalidPassword);
    // When
    final var controllerResponse = userController.create(createUserUseCase, request);
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
    final var newUser = new CreateUserDto(USER_NAME, USER_EMAIL, USER_PASSWORD);
    final var mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundByEmail(USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    final var createUserUseCase = new CreateUserUseCase(mockUserDataAccess, passwordService);
    final var foundUser = mockUserDataAccess.findByEmail(USER_EMAIL);
    request.body = newUser;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(request.body).isEqualTo(newUser);
    // When
    final var controllerResponse = userController.create(createUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserEmailAlreadyTakenException.message);
  }

  @Test
  @DisplayName("Valid request and email is not registered => 201")
  void emailNotRegistered() {
    final var newUser = new CreateUserDto(USER_NAME, USER_EMAIL, USER_PASSWORD);
    final var mockUserDataAccess = UserDataAccessMock.getMockForUserNotFoundByEmail(USER_EMAIL);
    final var createUserUseCase = new CreateUserUseCase(mockUserDataAccess, passwordService);
    final var foundUser = mockUserDataAccess.findByEmail(newUser.email);
    request.body = newUser;
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.body).isEqualTo(newUser);
    // When
    final var controllerResponse = userController.create(createUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(201);
  }

}
