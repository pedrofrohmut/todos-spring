package com.pedrofrohmut.todos.unit.web.controllers.tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.CreateTaskDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Task;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.errors.InvalidEntityException;
import com.pedrofrohmut.todos.domain.errors.InvalidTaskException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.tasks.CreateTaskUseCase;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.mocks.UserDataAccessMock;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.controllers.TaskController;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Task controller create method")
public class TaskControllerCreateTests {

  static final String TASK_NAME = "Task Name";
  static final String TASK_DESCRIPTION = "Task Description";
  static final String USER_ID = UUID.randomUUID().toString();
  static final String USER_NAME = "User Name";
  static final String USER_EMAIL = "user@mail.com";
  static final String USER_PASSWORD = "user_password";

  final TaskDataAccess mockTaskDataAccess;
  final UserDataAccess mockUserDataAccess;
  final CreateTaskUseCase createTaskUseCase;
  final TaskController taskController;
  final PasswordService passwordService;

  public TaskControllerCreateTests() {
    passwordService = new BcryptPasswordService();
    mockTaskDataAccess = mock(TaskDataAccess.class);
    mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundById(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    createTaskUseCase = new CreateTaskUseCase(mockTaskDataAccess, mockUserDataAccess);
    taskController = new TaskController();
  }

  AdaptedRequest<CreateTaskDto> request;

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
    final var controllerResponse = taskController.create(createTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestBodyException.message);
  }

  @Test
  @DisplayName("Invalid body.name => 400/message")
  void invalidBodyName() {
    final var invalidName = "";
    Exception nameErr = getNameErr(invalidName);
    request.authUserId = USER_ID;
    request.body = new CreateTaskDto(invalidName, TASK_DESCRIPTION);
    // Given
    assertThat(nameErr).isNotNull().isInstanceOf(InvalidTaskException.class);
    assertThat(request.body.name).isEqualTo(invalidName);
    // When
    final var controllerResponse = taskController.create(createTaskUseCase, request);
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
    Exception descriptionErr = getDescriptionErr(invalidDescription);
    request.body = new CreateTaskDto(TASK_NAME, invalidDescription);
    request.authUserId = USER_ID;
    // Given
    assertThat(descriptionErr).isNotNull().isInstanceOf(InvalidTaskException.class);
    assertThat(request.body.description).isEqualTo(invalidDescription);
    // When
    final var controllerResponse = taskController.create(createTaskUseCase, request);
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
    request.body = new CreateTaskDto(TASK_NAME, TASK_DESCRIPTION);
    // Given
    assertThat(request.authUserId).isNull();
    // When
    final var controllerResponse = taskController.create(createTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(401);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestAuthUserIdException.message);
  }

  @Test
  @DisplayName("Invalid authUserId => 400/message")
  void invalidAuthUserId() {
    final var invalidAuthUserId = "";
    Exception authUserIdErr = getAuthUserIdErr(invalidAuthUserId);
    request.body = new CreateTaskDto(TASK_NAME, TASK_DESCRIPTION);
    request.authUserId = invalidAuthUserId;
    // Given
    assertThat(authUserIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.authUserId).isEqualTo(invalidAuthUserId);
    // When
    final var controllerResponse = taskController.create(createTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(authUserIdErr.getMessage());
  }

  private Exception getAuthUserIdErr(String userId) {
    try {
      Entity.validateId(userId);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Valid request but user not found with request.authUserId => 400/message")
  void userNotFound() {
    final var mockUserDataAccess = UserDataAccessMock.getMockForUserNotFoundById(USER_ID);
    final var createTaskUseCase = new CreateTaskUseCase(mockTaskDataAccess, mockUserDataAccess);
    request.body = new CreateTaskDto(TASK_NAME, TASK_DESCRIPTION);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    request.authUserId = USER_ID;
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.authUserId).isEqualTo(USER_ID);
    // When
    final var controllerResponse = taskController.create(createTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserNotFoundByIdException.message);
  }

  @Test
  @DisplayName("Valid request and user found => 201")
  void validRequest() {
    final var nameErr = getNameErr(TASK_NAME);
    final var descriptionErr = getDescriptionErr(TASK_DESCRIPTION);
    final var authUserIdErr = getAuthUserIdErr(USER_ID);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    request.body = new CreateTaskDto(TASK_NAME, TASK_DESCRIPTION);
    request.authUserId = USER_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(nameErr).isNull();
    assertThat(descriptionErr).isNull();
    assertThat(authUserIdErr).isNull();
    assertThat(request.body.name).isEqualTo(TASK_NAME);
    assertThat(request.body.description).isEqualTo(TASK_DESCRIPTION);
    assertThat(request.authUserId).isEqualTo(USER_ID);
    // When
    final var controllerResponse = taskController.create(createTaskUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(201);
  }

}
