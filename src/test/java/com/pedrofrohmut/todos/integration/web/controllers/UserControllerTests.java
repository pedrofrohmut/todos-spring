package com.pedrofrohmut.todos.integration.web.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;

import com.pedrofrohmut.todos.domain.dtos.CreateUserDto;
import com.pedrofrohmut.todos.domain.dtos.SignInUserDto;
import com.pedrofrohmut.todos.domain.dtos.SignedUserDto;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.factories.UseCaseFactory;
import com.pedrofrohmut.todos.domain.services.JwtService;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.users.CreateUserUseCase;
import com.pedrofrohmut.todos.domain.usecases.users.GetSignedUserUseCase;
import com.pedrofrohmut.todos.domain.usecases.users.SignInUserUseCase;
import com.pedrofrohmut.todos.infra.factories.ConnectionFactory;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.infra.services.JjwtJwtService;
import com.pedrofrohmut.todos.utils.dataaccess.UserDataAccessUtil;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.controllers.UserController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
@DisplayName("User Controller Integration tests")
public class UserControllerTests {

  static final String USER_NAME = "User Name";
  static final String USER_EMAIL = "user@mail.com";
  static final String USER_PASSWORD = "user_password";

  final Connection connection;
  final UserDataAccessUtil userDataAccessUtil;
  final PasswordService passwordService;
  final UserController userController;
  final JwtService jwtService;

  public UserControllerTests() {
    connection = ConnectionFactory.getTestConnection();
    userDataAccessUtil = new UserDataAccessUtil(connection);
    passwordService = new BcryptPasswordService();
    userController = new UserController();
    jwtService = new JjwtJwtService();
  }

  @BeforeEach
  void beforeEach() {
    // Clean Up
    userDataAccessUtil.deleteAllUsers();
  }

  @Test
  @DisplayName("Create User registers")
  void createUser() {
    final var createUserUseCase = (CreateUserUseCase) UseCaseFactory.getInstance("CreateUserUseCase", connection);
    // Setup
    final var newUserBody = new CreateUserDto(USER_NAME, USER_EMAIL, USER_PASSWORD);
    final var foundUser = userDataAccessUtil.findByEmail(USER_EMAIL);
    final var request = new AdaptedRequest<CreateUserDto>(newUserBody, null, null);
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.body).isEqualTo(newUserBody);
    // When
    final var controllerResponse = userController.create(createUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(201);
    assertThat(controllerResponse.body).isNull();
  }

  @Test
  @DisplayName("Sign in a registered user")
  void signInUser() {
    final var signInUserUseCase = (SignInUserUseCase) UseCaseFactory.getInstance("SignInUserUseCase", connection);
    // Setup
    final var newUser = new User(USER_NAME, USER_EMAIL);
    final var passwordHash = passwordService.hashPassword(USER_PASSWORD);
    newUser.setPasswordHash(passwordHash);
    userDataAccessUtil.create(newUser);
    final var credentials = new SignInUserDto(USER_EMAIL, USER_PASSWORD);
    final var foundUser = userDataAccessUtil.findByEmail(USER_EMAIL);
    final var request = new AdaptedRequest<SignInUserDto>(credentials, null, null);
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(request.body).isEqualTo(credentials);
    // When
    final var controllerResponse = userController.signIn(signInUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(200);
    assertThat(((SignedUserDto) controllerResponse.body).id).isEqualTo(foundUser.getId());
    assertThat(((SignedUserDto) controllerResponse.body).name).isEqualTo(foundUser.getName());
    assertThat(((SignedUserDto) controllerResponse.body).email).isEqualTo(foundUser.getEmail());
    final var responseToken = ((SignedUserDto) controllerResponse.body).token;
    final var responseTokenUserId = jwtService.getUserIdFromToken(responseToken);
    assertThat(responseTokenUserId).isEqualTo(foundUser.getId());
  }

  @Test
  @DisplayName("Get signed user with the token id")
  void getSignedUser() {
    final var getSignedUserUseCase =
      (GetSignedUserUseCase) UseCaseFactory.getInstance("GetSignedUserUseCase", connection);
    // Setup
    final var newUser = new User(USER_NAME, USER_EMAIL);
    final var passwordHash = passwordService.hashPassword(USER_PASSWORD);
    newUser.setPasswordHash(passwordHash);
    userDataAccessUtil.create(newUser);
    final var foundUser = userDataAccessUtil.findByEmail(USER_EMAIL);
    final var token = jwtService.generateToken(foundUser.getId());
    final var userIdFromToken = jwtService.getUserIdFromToken(token);
    final var request = new AdaptedRequest<>(null, userIdFromToken, null);
    // Given
    assertThat(userIdFromToken).isEqualTo(foundUser.getId());
    // When
    final var controllerResponse = userController.getSigned(getSignedUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(200);
    assertThat(((SignedUserDto) controllerResponse.body).id).isEqualTo(foundUser.getId());
    assertThat(((SignedUserDto) controllerResponse.body).name).isEqualTo(foundUser.getName());
    assertThat(((SignedUserDto) controllerResponse.body).email).isEqualTo(foundUser.getEmail());
    final var responseToken = ((SignedUserDto) controllerResponse.body).token;
    final var responseTokenUserId = jwtService.getUserIdFromToken(responseToken);
    assertThat(responseTokenUserId).isEqualTo(foundUser.getId());
  }

}
