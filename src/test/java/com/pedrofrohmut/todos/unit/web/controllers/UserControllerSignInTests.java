package com.pedrofrohmut.todos.unit.web.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.SignInUserDto;
import com.pedrofrohmut.todos.domain.dtos.SignedUserDto;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.errors.PasswordAndHashDoNotMatchException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByEmailException;
import com.pedrofrohmut.todos.domain.services.JwtService;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.users.SignInUserUseCase;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.infra.services.JjwtJwtService;
import com.pedrofrohmut.todos.mocks.UserDataAccessMock;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.controllers.UserController;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("User Controller - sign in method")
public class UserControllerSignInTests {

  static final String USER_NAME = "User Name";
  static final String USER_EMAIL = "user@mail.com";
  static final String USER_PASSWORD = "user_password";

  final SignInUserUseCase signInUserUseCase;
  final UserController userController;
  final PasswordService passwordService;
  final JwtService jwtService;

  public UserControllerSignInTests() {
    final var mockUserDataAccess = mock(UserDataAccess.class);
    passwordService = new BcryptPasswordService();
    jwtService = new JjwtJwtService();
    signInUserUseCase = new SignInUserUseCase(mockUserDataAccess, passwordService, jwtService);
    userController = new UserController();
  }

  AdaptedRequest<SignInUserDto> request;

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
    final var controllerResponse = userController.signIn(signInUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestBodyException.message);
  }

  @Test
  @DisplayName("Invalid body.email => 400/message")
  void invalidBodyEmail() {
    final var invalidEmail = "invalid_email";
    // Setup
    final var emailErr = getEmailErr(invalidEmail);
    request.body = new SignInUserDto(invalidEmail, USER_PASSWORD);
    // Given
    assertThat(emailErr).isNotNull();
    assertThat(request.body.email).isEqualTo(invalidEmail);
    // When
    final var controllerResponse = userController.signIn(signInUserUseCase, request);
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
    // Setup
    final var passwordErr = getPasswordErr(invalidPassword);
    final var mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundByEmail(USER_NAME, USER_EMAIL, invalidPassword, passwordService);
    final var signInUserUseCase = new SignInUserUseCase(mockUserDataAccess, passwordService, jwtService);
    request.body = new SignInUserDto(USER_EMAIL, invalidPassword);
    // Given
    assertThat(passwordErr).isNotNull();
    assertThat(request.body.password).isEqualTo(invalidPassword);
    // When
    final var controllerResponse = userController.signIn(signInUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(passwordErr.getMessage());
  }

  private Exception getPasswordErr(String invalidPassword) {
    try {
      User.validatePassword(invalidPassword);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Valid request and user not found by email => 400/message")
  void userNotFoundByEmail() {
    final var notRegisteredEmail = USER_EMAIL;
    final var mockUserDataAccess = UserDataAccessMock.getMockForUserNotFoundByEmail(notRegisteredEmail);
    final var signInUserUseCase = new SignInUserUseCase(mockUserDataAccess, passwordService, jwtService);
    final var foundUser = mockUserDataAccess.findByEmail(notRegisteredEmail);
    request.body = new SignInUserDto(notRegisteredEmail, USER_PASSWORD);
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.body.email).isEqualTo(notRegisteredEmail);
    // Then
    final var controllerResponse = userController.signIn(signInUserUseCase, request);
    // When
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserNotFoundByEmailException.message);
  }

  @Test
  @DisplayName("Valid request but password do not match the hash => 400/message")
  void passwordDoNotMatchHash() {
    final var notMatchingPassword = "not_matching_password";
    final var mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundByEmail(USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    final var signInUserUseCase = new SignInUserUseCase(mockUserDataAccess, passwordService, jwtService);
    final var isMatch = passwordService.comparePasswordAndHash(notMatchingPassword, USER_PASSWORD);
    request.body = new SignInUserDto(USER_EMAIL, notMatchingPassword);
    // Given
    assertThat(isMatch).isFalse();
    assertThat(request.body.password).isEqualTo(notMatchingPassword);
    // When
    final var controllerResponse = userController.signIn(signInUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(PasswordAndHashDoNotMatchException.message);
  }

  @Test
  @DisplayName("Valid request, user registered and password matches => 200/{ id, name, email, token }")
  void emailRegisteredAndPasswordMatches() {
    final var registeredEmail = USER_EMAIL;
    final var matchingPassword = USER_PASSWORD;
    final var mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundByEmail(USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    final var signInUserUseCase = new SignInUserUseCase(mockUserDataAccess, passwordService, jwtService);
    final var foundUser = mockUserDataAccess.findByEmail(registeredEmail);
    final var isMatch = passwordService.comparePasswordAndHash(matchingPassword, foundUser.getPasswordHash());
    request.body = new SignInUserDto(registeredEmail, matchingPassword);
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(isMatch).isTrue();
    assertThat(request.body.email).isEqualTo(registeredEmail);
    assertThat(request.body.password).isEqualTo(matchingPassword);
    // When
    final var controllerResponse = userController.signIn(signInUserUseCase, request);
    // Then
    assertControllerResponse200WithCorrectBody(
        controllerResponse.httpStatus, (SignedUserDto) controllerResponse.body, foundUser);
  }

  private void assertControllerResponse200WithCorrectBody(int status, SignedUserDto body, User foundUser) {
    assertThat(status).isEqualTo(200);
    assertThat(body.id).isEqualTo(foundUser.getId());
    assertThat(body.name).isEqualTo(foundUser.getName());
    assertThat(body.email).isEqualTo(foundUser.getEmail());
    assertThat(body.token).isNotNull();
  }

}
