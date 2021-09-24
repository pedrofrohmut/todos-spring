package com.pedrofrohmut.todos.unit.web.controllers.todos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.TodoDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.errors.InvalidEntityException;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.todos.ClearCompleteTodosByTaskIdUseCase;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.mocks.TaskDataAccessMock;
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
@DisplayName("Todo controller clear complete by task id")
public class TodoControllerClearCompleteByTaskIdTests {

  static final String TASK_ID = UUID.randomUUID().toString();
  static final String TASK_NAME = "Task Name";
  static final String TASK_DESCRIPTION = "Task Description";
  static final String USER_ID = UUID.randomUUID().toString();
  static final String USER_NAME = "User Name";
  static final String USER_EMAIL = "user@mail.com";
  static final String USER_PASSWORD = "user_password";

  final PasswordService passwordService;
  final TodoDataAccess mockTodoDataAccess;
  final TaskDataAccess mockTaskDataAccess;
  final UserDataAccess mockUserDataAccess;
  final ClearCompleteTodosByTaskIdUseCase clearCompleteTodosByTaskIdUseCase;
  final TodoController todoController;

  public TodoControllerClearCompleteByTaskIdTests() {
    passwordService = new BcryptPasswordService();
    mockTodoDataAccess = mock(TodoDataAccess.class);
    mockTaskDataAccess =
      TaskDataAccessMock.getMockForTaskFoundById(TASK_ID, TASK_NAME, TASK_DESCRIPTION, USER_ID);
    mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundById(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    clearCompleteTodosByTaskIdUseCase = new ClearCompleteTodosByTaskIdUseCase(mockTodoDataAccess, mockTaskDataAccess, mockUserDataAccess);
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
    request.param = TASK_ID;
    // Given
    assertThat(request.authUserId).isNull();
    // When
    final var controllerResponse = todoController.clearCompleteByTaskId(clearCompleteTodosByTaskIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(401);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestAuthUserIdException.message);
  }

  @Test
  @DisplayName("Invalid authUserId => 400/message")
  void invalidAuthUserId() {
    final var invalidAuthUserId = "";
    final var authUserIdErr = getAuthUserIdErr(invalidAuthUserId);
    request.param = TASK_ID;
    request.authUserId = invalidAuthUserId;
    // Given
    assertThat(authUserIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.authUserId).isEqualTo(invalidAuthUserId);
    // When
    final var controllerResponse = todoController.clearCompleteByTaskId(clearCompleteTodosByTaskIdUseCase, request);
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
  @DisplayName("Null request.param for taskId => 400/message")
  void nullRequestParam() {
    request.authUserId = USER_ID;
    // Given
    assertThat(request.param).isNull();
    // When
    final var controllerResponse = todoController.clearCompleteByTaskId(clearCompleteTodosByTaskIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestParametersException.message);
  }

  @Test
  @DisplayName("Invalid request.param for taskId => 400/message")
  void invalidRequestParam() {
    final var invalidTaskId = "";
    final var todoIdErr = getTaskIdErr(invalidTaskId);
    request.authUserId = USER_ID;
    request.param = invalidTaskId;
    // Given
    assertThat(todoIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.param).isEqualTo(invalidTaskId);
    // When
    final var controllerResponse = todoController.clearCompleteByTaskId(clearCompleteTodosByTaskIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(todoIdErr.getMessage());
  }

  private Exception getTaskIdErr(String todoId) {
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
    final var clearCompleteTodosByTaskIdUseCase =
      new ClearCompleteTodosByTaskIdUseCase(mockTodoDataAccess, mockTaskDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    request.authUserId = USER_ID;
    request.param = TASK_ID;
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.authUserId).isEqualTo(USER_ID);
    // When
    final var controllerResponse = todoController.clearCompleteByTaskId(clearCompleteTodosByTaskIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserNotFoundByIdException.message);
  }

  @Test
  @DisplayName("Valid request and user found but task not found => 400/message")
  void userFoundButTaskNotFound() {
    final var mockTaskDataAccess = TaskDataAccessMock.getMockForTaskNotFoundById(TASK_ID);
    final var clearCompleteTodosByTaskIdUseCase =
      new ClearCompleteTodosByTaskIdUseCase(mockTodoDataAccess, mockTaskDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTask = mockTaskDataAccess.findById(TASK_ID);
    request.authUserId = USER_ID;
    request.param = TASK_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTask).isNull();
    assertThat(request.param).isEqualTo(TASK_ID);
    // When
    final var controllerResponse = todoController.clearCompleteByTaskId(clearCompleteTodosByTaskIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(TaskNotFoundByIdException.message);
  }

  @Test
  @DisplayName("Valid request, user and task found but task.userId != authUserId => 401/message")
  void userNotResourceOwnerOfTodo() {
    final var otherUserId = UUID.randomUUID().toString();
    final var otherUserTaskId = UUID.randomUUID().toString();
    final var mockTaskDataAccess =
      TaskDataAccessMock.getMockForTaskFoundById(otherUserTaskId, TASK_NAME, TASK_DESCRIPTION, otherUserId);
    final var clearCompleteTodosByTaskIdUseCase =
      new ClearCompleteTodosByTaskIdUseCase(mockTodoDataAccess, mockTaskDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTask = mockTaskDataAccess.findById(otherUserTaskId);
    request.authUserId = USER_ID;
    request.param = otherUserTaskId;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTask).isNotNull();
    assertThat(foundTask.getUserId()).isNotEqualTo(USER_ID);
    assertThat(request.authUserId).isEqualTo(USER_ID);
    assertThat(request.param).isEqualTo(otherUserTaskId);
    // When
    final var controllerResponse = todoController.clearCompleteByTaskId(clearCompleteTodosByTaskIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(401);
    assertThat(controllerResponse.body.toString()).contains(UserNotResourceOwnerException.message);
  }

  @Test
  @DisplayName("Valid request, user and task found => 204")
  void userAndTaskFound() {
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTask = mockTaskDataAccess.findById(TASK_ID);
    request.authUserId = USER_ID;
    request.param = TASK_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTask).isNotNull();
    assertThat(request.authUserId).isEqualTo(USER_ID);
    assertThat(request.param).isEqualTo(TASK_ID);
    // When
    final var controllerResponse = todoController.clearCompleteByTaskId(clearCompleteTodosByTaskIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(204);
    assertThat(controllerResponse.body).isNull();
    verify(mockTodoDataAccess, times(1)).clearCompleteByTaskId(TASK_ID);
  }

}
