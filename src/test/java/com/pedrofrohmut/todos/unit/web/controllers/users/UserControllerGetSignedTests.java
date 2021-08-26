package com.pedrofrohmut.todos.unit.web.controllers.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.SignedUserDto;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.services.JwtService;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.users.GetSignedUserUseCase;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.infra.services.JjwtJwtService;
import com.pedrofrohmut.todos.mocks.UserDataAccessMock;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.controllers.UserController;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("User Controller - get signed method")
public class UserControllerGetSignedTests {

  static final String USER_NAME = "User Name";
  static final String USER_EMAIL = "user@mail.com";
  static final String USER_PASSWORD = "user_password";

  final GetSignedUserUseCase getSignedUserUseCase;
  final UserController userController;
  final JwtService jwtService;
  final PasswordService passwordService;

  public UserControllerGetSignedTests() {
    final var mockUserDataAccess = mock(UserDataAccess.class);
    jwtService = new JjwtJwtService();
    getSignedUserUseCase = new GetSignedUserUseCase(mockUserDataAccess, jwtService);
    userController = new UserController();
    passwordService = new BcryptPasswordService();
  }

  AdaptedRequest<?> request;

  @BeforeEach
  void beforeEach() {
    request = new AdaptedRequest<>(null, null, null);
  }

  @Test
  @DisplayName("Auth user id is null => 401/message")
  void nullAuthUserId() {
    // Given
    assertThat(request.authUserId).isNull();
    // When
    final var controllerResponse = userController.getSigned(getSignedUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestAuthUserIdException.message);
  }

  @Test
  @DisplayName("Valid request.authUserId but user not found by authUserId => 400/message")
  void userNotFoundByAuthUserId() {
    final var userId = UUID.randomUUID().toString();
    final var mockUserDataAccess = UserDataAccessMock.getMockForUserNotFoundById(userId);
    final var getSignedUserUseCase = new GetSignedUserUseCase(mockUserDataAccess, jwtService);
    final var foundUser = mockUserDataAccess.findById(userId);
    request.authUserId = userId;
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.authUserId).isEqualTo(userId);
    // When
    final var controllerResponse = userController.getSigned(getSignedUserUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserNotFoundByIdException.message);
  }

  @Test
  @DisplayName("Valid request.authUserId and user found by authUserId => 200/{ id, name, email, token }")
  void userFoundByAuthUserId() {
    final var userId = UUID.randomUUID().toString();
    final var mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundById(userId, USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    final var getSignedUserUseCase = new GetSignedUserUseCase(mockUserDataAccess, jwtService);
    final var foundUser = mockUserDataAccess.findById(userId);
    request.authUserId = userId;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(request.authUserId).isEqualTo(userId);
    // When
    final var controllerResponse = userController.getSigned(getSignedUserUseCase, request);
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
