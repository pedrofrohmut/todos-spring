package com.pedrofrohmut.todos.unit.web.controllers.todos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.TodoDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.errors.InvalidEntityException;
import com.pedrofrohmut.todos.domain.errors.TodoNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.todos.SetDoneTodoUseCase;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.mocks.TodoDataAccessMock;
import com.pedrofrohmut.todos.mocks.UserDataAccessMock;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.controllers.TodoController;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Todo controller set done")
public class TodoControllerSetDoneTests {

  static final String TODO_ID = UUID.randomUUID().toString();
  static final String TODO_TITLE = "Todo Title";
  static final String TODO_DESCRIPTION = "Todo Description";
  static final boolean TODO_IS_DONE = false;
  static final String TASK_ID = UUID.randomUUID().toString();
  static final String USER_ID = UUID.randomUUID().toString();
  static final String USER_NAME = "User Name";
  static final String USER_EMAIL = "user@mail.com";
  static final String USER_PASSWORD = "user_password";

  final PasswordService passwordService;
  final TodoDataAccess mockTodoDataAccess;
  final UserDataAccess mockUserDataAccess;
  final SetDoneTodoUseCase setDoneTodoUseCase;
  final TodoController todoController;

  public TodoControllerSetDoneTests() {
    passwordService = new BcryptPasswordService();
    mockTodoDataAccess =
      TodoDataAccessMock.getMockForTodoFoundById(TODO_ID, TODO_TITLE, TODO_DESCRIPTION, TODO_IS_DONE, TASK_ID, USER_ID);
    mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundById(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    setDoneTodoUseCase = new SetDoneTodoUseCase(mockTodoDataAccess, mockUserDataAccess);
    todoController = new TodoController();
  }

  AdaptedRequest<?> request;

  @BeforeEach
  void beforeEach() {
    request = new AdaptedRequest<>(null, null, null);
  }

  @Test
  @DisplayName("Null authUserId => 401/message")
  void nullAuthUserId() {
    request.param = TODO_ID;
    // Given
    assertThat(request.authUserId).isNull();
    // When
    final var controllerResponse = todoController.setDone(setDoneTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(401);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestAuthUserIdException.message);
  }

  @Test
  @DisplayName("Invalid authUserId => 400/message")
  void invalidAuthUserId() {
    final var invalidAuthUserId = "";
    final var authUserIdErr = getAuthUserIdErr(invalidAuthUserId);
    request.param = TODO_ID;
    request.authUserId = invalidAuthUserId;
    // Given
    assertThat(authUserIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.authUserId).isEqualTo(invalidAuthUserId);
    // When
    final var controllerResponse = todoController.setDone(setDoneTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(authUserIdErr.getMessage());
  }

  private Exception getAuthUserIdErr(String authUserId) {
    try {
      Entity.validateId(authUserId);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Null request.param for todoId => 400/message")
  void nullRequestParam() {
    request.authUserId = USER_ID;
    // Given
    assertThat(request.param).isNull();
    // When
    final var controllerResponse = todoController.setDone(setDoneTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestParametersException.message);
  }

  @Test
  @DisplayName("Invalid request.param for todoId => 400/message")
  void invalidRequestParam() {
    final var invalidTodoId = "";
    final var todoIdErr = getTodoIdErr(invalidTodoId);
    request.authUserId = USER_ID;
    request.param = invalidTodoId;
    // Given
    assertThat(todoIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.param).isEqualTo(invalidTodoId);
    // When
    final var controllerResponse = todoController.setDone(setDoneTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(todoIdErr.getMessage());
  }

  private Exception getTodoIdErr(String todoId) {
    try {
      Entity.validateId(todoId);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Valid request but user not found => 400/message")
  void userNotFound() {
    final var mockUserDataAccess = UserDataAccessMock.getMockForUserNotFoundById(USER_ID);
    final var setDoneTodoUseCase = new SetDoneTodoUseCase(mockTodoDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    request.authUserId = USER_ID;
    request.param = TODO_ID;
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.authUserId).isEqualTo(USER_ID);
    // When
    final var controllerResponse = todoController.setDone(setDoneTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserNotFoundByIdException.message);
  }

  @Test
  @DisplayName("Valid request and user found but todo not found => 400/message")
  void userFoundButTodoNotFound() {
    final var mockTodoDataAccess = TodoDataAccessMock.getMockForTodoNotFoundById(TODO_ID);
    final var setDoneTodoUseCase = new SetDoneTodoUseCase(mockTodoDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTodo = mockTodoDataAccess.findById(TODO_ID);
    request.authUserId = USER_ID;
    request.param = TODO_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTodo).isNull();
    assertThat(request.param).isEqualTo(TODO_ID);
    // When
    final var controllerResponse = todoController.setDone(setDoneTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(TodoNotFoundByIdException.message);
  }

  @Test
  @DisplayName("Valid request, user and todo found but todo.userId != authUserId => 401/message")
  void userNotResourceOwnerOfTodo() {
    final var otherUserId = UUID.randomUUID().toString();
    final var otherUserTaskId = UUID.randomUUID().toString();
    final var otherUserTodoId = UUID.randomUUID().toString();
    final var mockTodoDataAccess =
      TodoDataAccessMock.getMockForTodoFoundById(
          otherUserTodoId, TODO_TITLE, TODO_DESCRIPTION, TODO_IS_DONE, otherUserTaskId, otherUserId);
    final var setDoneTodoUseCase = new SetDoneTodoUseCase(mockTodoDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTodo = mockTodoDataAccess.findById(otherUserTodoId);
    request.authUserId = USER_ID;
    request.param = otherUserTodoId;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTodo).isNotNull();
    assertThat(foundTodo.getUserId()).isNotEqualTo(USER_ID);
    assertThat(request.authUserId).isEqualTo(USER_ID);
    assertThat(request.param).isEqualTo(otherUserTodoId);
    // When
    final var controllerResponse = todoController.setDone(setDoneTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(401);
    assertThat(controllerResponse.body.toString()).contains(UserNotResourceOwnerException.message);
  }

  @Test
  @DisplayName("Valid request, user and todo found => 204")
  void userAndTodoFound() {
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTodo = mockTodoDataAccess.findById(TODO_ID);
    request.authUserId = USER_ID;
    request.param = TODO_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTodo).isNotNull();
    assertThat(request.authUserId).isEqualTo(USER_ID);
    assertThat(request.param).isEqualTo(TODO_ID);
    // When
    final var controllerResponse = todoController.setDone(setDoneTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(204);
    assertThat(controllerResponse.body).isNull();
    verify(mockTodoDataAccess, times(1)).setDone(TODO_ID);
  }

}
