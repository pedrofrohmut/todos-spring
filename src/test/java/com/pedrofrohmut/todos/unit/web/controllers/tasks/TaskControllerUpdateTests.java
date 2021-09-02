package com.pedrofrohmut.todos.unit.web.controllers.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.UpdateTaskDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Task;
import com.pedrofrohmut.todos.domain.errors.InvalidEntityException;
import com.pedrofrohmut.todos.domain.errors.InvalidTaskException;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.tasks.UpdateTaskUseCase;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.mocks.TaskDataAccessMock;
import com.pedrofrohmut.todos.mocks.UserDataAccessMock;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.controllers.TaskController;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Task controller update task")
public class TaskControllerUpdateTests {

  static final String USER_ID = UUID.randomUUID().toString();
  static final String USER_NAME = "User Name";
  static final String USER_EMAIL = "user@mail.com";
  static final String USER_PASSWORD = "user_password";
  static final String TASK_ID = UUID.randomUUID().toString();
  static final String TASK_NAME = "Task Name";
  static final String TASK_DESCRIPTION = "Task Description";

  final TaskController taskController;
  final PasswordService passwordService;

  public TaskControllerUpdateTests() {
    passwordService = new BcryptPasswordService();
    taskController = new TaskController();
  }

  TaskDataAccess mockTaskDataAccess;
  UserDataAccess mockUserDataAccess;
  AdaptedRequest<UpdateTaskDto> request;
  UpdateTaskUseCase updateTaskUseCase;

  @BeforeEach
  void beforeEach() {
    mockTaskDataAccess = TaskDataAccessMock.getMockForTaskFoundById(TASK_ID, TASK_NAME, TASK_DESCRIPTION, USER_ID);
    mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundById(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    updateTaskUseCase = new UpdateTaskUseCase(mockTaskDataAccess, mockUserDataAccess);
    request = new AdaptedRequest<>(null, null, null);
  }

  @Test
  @DisplayName("Null body => 400/message")
  void nullBody() {
    // Given
    assertThat(request.body).isNull();
    // When
    final var controllerResponse = taskController.update(updateTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestBodyException.message);
  }

  @Test
  @DisplayName("Invalid body.name => 400/message")
  void invalidBodyName() {
    final var invalidName = "";
    final var nameErr = getNameErr(invalidName);
    request.body = new UpdateTaskDto(invalidName, TASK_DESCRIPTION);
    request.authUserId = USER_ID;
    request.param = TASK_ID;
    // Given
    assertThat(nameErr).isNotNull().isInstanceOf(InvalidTaskException.class);
    assertThat(request.body.name).isEqualTo(invalidName);
    // When
    final var controllerResponse = taskController.update(updateTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(nameErr.getMessage());
  }

  private Exception getNameErr(String name) {
    try {
      Task.validateName(name);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Invalid body.description => 400/message")
  void invalidBodyDescription() {
    final var invalidDescription = "Lorem eveniet perferendis a amet expedita? Cumque maxime quaerat nulla modi exercitationem corrupti, obcaecati! Inventore temporibus quidem iure maiores fugit assumenda qui? Nihil. Consectetur impedit magnam iusto sint culpa. Omnis consequuntur quia consequuntur";
    final var descriptionErr = getDescriptionErr(invalidDescription);
    request.body = new UpdateTaskDto(TASK_NAME, invalidDescription);
    request.authUserId = USER_ID;
    request.param = TASK_ID;
    // Given
    assertThat(descriptionErr).isNotNull().isInstanceOf(InvalidTaskException.class);
    assertThat(request.body.description).isEqualTo(invalidDescription);
    // When
    final var controllerResponse = taskController.update(updateTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(descriptionErr.getMessage());
  }

  private Exception getDescriptionErr(String description) {
    try {
      Task.validateDescription(description);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Null authUserId => 401/message")
  void nullAuthUserId() {
    request.body = new UpdateTaskDto(TASK_NAME, TASK_DESCRIPTION);
    request.param = TASK_ID;
    // Given
    assertThat(request.authUserId).isNull();
    // When
    final var controllerResponse = taskController.update(updateTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(401);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestAuthUserIdException.message);
  }

  @Test
  @DisplayName("Invalid authUserId => 400/message")
  void invalidAuthUserId() {
    final var invalidAuthUserId = "";
    final var authUserIdErr = getAuthUserIdErr(invalidAuthUserId);
    request.body = new UpdateTaskDto(TASK_NAME, TASK_DESCRIPTION);
    request.authUserId = invalidAuthUserId;
    request.param = TASK_ID;
    // Given
    assertThat(authUserIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.authUserId).isEqualTo(invalidAuthUserId);
    // When
    final var controllerResponse = taskController.update(updateTaskUseCase, request);
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
    request.body = new UpdateTaskDto(TASK_NAME, TASK_DESCRIPTION);
    request.authUserId = USER_ID;
    // Given
    assertThat(request.param).isNull();
    // When
    final var controllerResponse = taskController.update(updateTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestParametersException.message);
  }

  @Test
  @DisplayName("Invalid request.param for taskId => 400/message")
  void invalidRequestParam() {
    final var invalidTaskId = "";
    final var taskIdErr = getTaskIdErr(invalidTaskId);
    request.body = new UpdateTaskDto(TASK_NAME, TASK_DESCRIPTION);
    request.authUserId = USER_ID;
    request.param = invalidTaskId;
    // Given
    assertThat(taskIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.param).isEqualTo(invalidTaskId);
    // WHen
    final var controllerResponse = taskController.update(updateTaskUseCase, request);
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
    final var updateTaskUseCase = new UpdateTaskUseCase(mockTaskDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    request.body = new UpdateTaskDto(TASK_NAME, TASK_DESCRIPTION);
    request.authUserId = USER_ID;
    request.param = TASK_ID;
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.authUserId).isEqualTo(USER_ID);
    // When
    final var controllerResponse = taskController.update(updateTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserNotFoundByIdException.message);
    verify(mockUserDataAccess, times(2)).findById(USER_ID);
    verify(mockTaskDataAccess, times(0)).findById(TASK_ID);
    verify(mockTaskDataAccess, times(0)).update(any(Task.class));
  }

  @Test
  @DisplayName("Valid request and user found but task not found by request.param for taskId => 400/message")
  void userFoundButTaskNotFound() {
    final var mockTaskDataAccess = TaskDataAccessMock.getMockForTaskNotFoundById(TASK_ID);
    final var updateTaskUseCase = new UpdateTaskUseCase(mockTaskDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTask = mockTaskDataAccess.findById(TASK_ID);
    request.body = new UpdateTaskDto(TASK_NAME, TASK_DESCRIPTION);
    request.authUserId = USER_ID;
    request.param = TASK_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTask).isNull();
    assertThat(request.param).isEqualTo(TASK_ID);
    // When
    final var controllerResponse = taskController.update(updateTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(TaskNotFoundByIdException.message);
    verify(mockUserDataAccess, times(2)).findById(USER_ID);
    verify(mockTaskDataAccess, times(2)).findById(TASK_ID);
    verify(mockTaskDataAccess, times(0)).update(any(Task.class));
  }

  @Test
  @DisplayName("Valid request, user and task found => 204")
  void userAndTaskFound() {
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTask = mockTaskDataAccess.findById(TASK_ID);
    final var nameErr = getNameErr(TASK_NAME);
    final var descriptionErr = getDescriptionErr(TASK_DESCRIPTION);
    final var authUserIdErr = getAuthUserIdErr(USER_ID);
    final var taskIdErr = getTaskIdErr(TASK_ID);
    request.body = new UpdateTaskDto(TASK_NAME, TASK_DESCRIPTION);
    request.authUserId = USER_ID;
    request.param = TASK_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTask).isNotNull();
    assertThat(nameErr).isNull();
    assertThat(descriptionErr).isNull();
    assertThat(authUserIdErr).isNull();
    assertThat(taskIdErr).isNull();
    assertThat(request.body.name).isEqualTo(TASK_NAME);
    assertThat(request.body.description).isEqualTo(TASK_DESCRIPTION);
    assertThat(request.authUserId).isEqualTo(USER_ID);
    assertThat(request.param).isEqualTo(TASK_ID);
    // When
    final var controllerResponse = taskController.update(updateTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(204);
    assertThat(controllerResponse.body).isNull();
    verify(mockUserDataAccess, times(2)).findById(USER_ID);
    verify(mockTaskDataAccess, times(2)).findById(TASK_ID);
    verify(mockTaskDataAccess, times(1)).update(any(Task.class));
  }

}
