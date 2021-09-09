package com.pedrofrohmut.todos.unit.web.controllers.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Task;
import com.pedrofrohmut.todos.domain.errors.InvalidEntityException;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.tasks.DeleteTaskUseCase;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.mocks.TaskDataAccessMock;
import com.pedrofrohmut.todos.mocks.UserDataAccessMock;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.controllers.TaskController;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Task controller delete method")
public class TaskControllerDeleteTests {

  static final String USER_ID = UUID.randomUUID().toString();
  static final String USER_NAME = "User Name";
  static final String USER_EMAIL = "user@mail.com";
  static final String USER_PASSWORD = "user_password";
  static final String TASK_ID = UUID.randomUUID().toString();
  static final String TASK_NAME = "Task Name";
  static final String TASK_DESCRIPTION = "Task Description";

  final TaskController taskController;
  final PasswordService passwordService;

  public TaskControllerDeleteTests() {
    passwordService = new BcryptPasswordService();
    taskController = new TaskController();
  }

  TaskDataAccess mockTaskDataAccess;
  UserDataAccess mockUserDataAccess;
  AdaptedRequest<?> request;
  DeleteTaskUseCase deleteTaskUseCase;

  @BeforeEach
  void beforeEach() {
    mockTaskDataAccess = TaskDataAccessMock.getMockForTaskFoundById(TASK_ID, TASK_NAME, TASK_DESCRIPTION, USER_ID);
    mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundById(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    deleteTaskUseCase = new DeleteTaskUseCase(mockTaskDataAccess, mockUserDataAccess);
    request = new AdaptedRequest<>(null, null, null);
  }

  @Test
  @DisplayName("Null authUserId => 401/message")
  void nullAuthUserId() {
    request.param = TASK_ID;
    // Given
    assertThat(request.authUserId).isNull();
    // When
    final var controllerResponse = taskController.delete(deleteTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(401);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestAuthUserIdException.message);
  }


  @Test
  @DisplayName("Invalid authUserId => 400/message")
  void invalidAuthUserId() {
    final var invalidAuthUserId = "";
    final var authUserIdErr = getAuthUserIdErr(invalidAuthUserId);
    request.authUserId = invalidAuthUserId;
    request.param = TASK_ID;
    // Given
    assertThat(authUserIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.authUserId).isEqualTo(invalidAuthUserId);
    // When
    final var controllerResponse = taskController.delete(deleteTaskUseCase, request);
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
    final var controllerResponse = taskController.delete(deleteTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestParametersException.message);
  }

  @Test
  @DisplayName("Invalid request.param for taskId => 400/message")
  void invalidRequestParam() {
    final var invalidTaskId = "";
    final var taskIdErr = getTaskIdErr(invalidTaskId);
    request.authUserId = USER_ID;
    request.param = invalidTaskId;
    // Given
    assertThat(taskIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.param).isEqualTo(invalidTaskId);
    // When
    final var controllerResponse = taskController.delete(deleteTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(taskIdErr.getMessage());
  }

  private Exception getTaskIdErr(String taskId) {
    try {
      Entity.validateId(taskId);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Valid request but user not found by request.authUserId => 400/message")
  void userNotFound() {
    final var mockUserDataAccess = UserDataAccessMock.getMockForUserNotFoundById(USER_ID);
    final var deleteTaskUseCase = new DeleteTaskUseCase(mockTaskDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    request.authUserId = USER_ID;
    request.param = TASK_ID;
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.authUserId).isEqualTo(USER_ID);
    // When
    final var controllerResponse = taskController.delete(deleteTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserNotFoundByIdException.message);
    verify(mockUserDataAccess, times(2)).findById(USER_ID);
    verify(mockTaskDataAccess, times(0)).findById(TASK_ID);
    verify(mockTaskDataAccess, times(0)).delete(TASK_ID);
  }

  @Test
  @DisplayName("Valid request and user found but task not found by request.param for taskId => 400/message")
  void userFoundButTaskNotFound() {
    final var mockTaskDataAccess = TaskDataAccessMock.getMockForTaskNotFoundById(TASK_ID);
    final var deleteTaskUseCase = new DeleteTaskUseCase(mockTaskDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTask = mockTaskDataAccess.findById(TASK_ID);
    request.authUserId = USER_ID;
    request.param = TASK_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTask).isNull();
    assertThat(request.param).isEqualTo(TASK_ID);
    // When
    final var controllerResponse = taskController.delete(deleteTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(TaskNotFoundByIdException.message);
    verify(mockUserDataAccess, times(2)).findById(USER_ID);
    verify(mockTaskDataAccess, times(2)).findById(TASK_ID);
    verify(mockTaskDataAccess, times(0)).delete(TASK_ID);
  }

  @Test
  @DisplayName("Valid request, user and task found => 204")
  void userAndTaskFound() {
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTask = mockTaskDataAccess.findById(TASK_ID);
    final var authUserIdErr = getAuthUserIdErr(USER_ID);
    final var taskIdErr = getTaskIdErr(TASK_ID);
    request.authUserId = USER_ID;
    request.param = TASK_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTask).isNotNull();
    assertThat(authUserIdErr).isNull();
    assertThat(taskIdErr).isNull();
    assertThat(request.authUserId).isEqualTo(USER_ID);
    assertThat(request.param).isEqualTo(TASK_ID);
    // When
    final var controllerResponse = taskController.delete(deleteTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(204);
    assertThat(controllerResponse.body).isNull();
    verify(mockUserDataAccess, times(2)).findById(USER_ID);
    verify(mockTaskDataAccess, times(2)).findById(TASK_ID);
    verify(mockTaskDataAccess, times(1)).delete(TASK_ID);
  }

}
