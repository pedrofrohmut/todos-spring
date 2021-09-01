package com.pedrofrohmut.todos.unit.web.controllers.tasks;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.TaskDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Task;
import com.pedrofrohmut.todos.domain.errors.InvalidEntityException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.tasks.FindTasksByUserIdUseCase;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.mocks.TaskDataAccessMock;
import com.pedrofrohmut.todos.mocks.UserDataAccessMock;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.controllers.TaskController;
import com.pedrofrohmut.todos.web.dtos.ControllerResponseDto;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Task controller find by user id")
public class TaskControllerFindByUserIdTests {

  static final String USER_ID = UUID.randomUUID().toString();
  static final String USER_NAME = "User Name";
  static final String USER_EMAIL = "user@mail.com";
  static final String USER_PASSWORD = "user_password";
  static final List<Task> TASKS = new ArrayList<>();

  static {
    TASKS.add(new Task(UUID.randomUUID().toString(), "Task Name 1", "Task Description 1", USER_ID));
    TASKS.add(new Task(UUID.randomUUID().toString(), "Task Name 2", "Task Description 2", USER_ID));
    TASKS.add(new Task(UUID.randomUUID().toString(), "Task Name 3", "Task Description 3", USER_ID));
    TASKS.add(new Task(UUID.randomUUID().toString(), "Task Name 4", "Task Description 4", USER_ID));
    TASKS.add(new Task(UUID.randomUUID().toString(), "Task Name 5", "Task Description 5", USER_ID));
  }

  final PasswordService passwordService;
  final TaskDataAccess mockTaskDataAccess;
  final UserDataAccess mockUserDataAccess;
  final FindTasksByUserIdUseCase findTasksByUserIdUseCase;
  final TaskController taskController;

  public TaskControllerFindByUserIdTests() {
    passwordService = new BcryptPasswordService();
    mockTaskDataAccess = TaskDataAccessMock.getMockForTasksFoundByUserId(USER_ID, TASKS);
    mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundById(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    findTasksByUserIdUseCase = new FindTasksByUserIdUseCase(mockTaskDataAccess, mockUserDataAccess);
    taskController = new TaskController();
  }

  AdaptedRequest<?> request;

  @BeforeEach
  void beforeEach() {
    request = new AdaptedRequest<>(null, null, null);
  }

  @Test
  @DisplayName("Null authUserId => 401/message")
  void nullAuthUserId() {
    request.param = USER_ID;
    // Given
    assertThat(request.authUserId).isNull();
    // When
    final var controllerResponse = taskController.findByUserId(findTasksByUserIdUseCase, request);
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
    request.param = USER_ID;
    // Given
    assertThat(authUserIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.authUserId).isEqualTo(invalidAuthUserId);
    // When
    final var controllerResponse = taskController.findByUserId(findTasksByUserIdUseCase, request);
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
  @DisplayName("Null request.param for userId => 400/message")
  void nullRequestParam() {
    request.authUserId = USER_ID;
    // Given
    assertThat(request.param).isNull();
    // When
    final var controllerResponse = taskController.findByUserId(findTasksByUserIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestParametersException.message);
  }

  @Test
  @DisplayName("Invalid request.param for userId => 400/message")
  void invalidRequestParam() {
    final var invalidUserId = "";
    final var userIdErr = getUserIdErr(invalidUserId);
    request.authUserId = USER_ID;
    request.param = invalidUserId;
    // Given
    assertThat(request.param).isEqualTo(invalidUserId);
    assertThat(userIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    // When
    final var controllerResponse = taskController.findByUserId(findTasksByUserIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(userIdErr.getMessage());
  }

  private Exception getUserIdErr(String userId) {
    try {
      Entity.validateId(userId);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Request.param and authUserId do not match => 401/message")
  void requestParamAndAuthUserIdDontMatch() {
    final var notMatchingUserId = UUID.randomUUID().toString();
    request.authUserId = USER_ID;
    request.param = notMatchingUserId;
    // Given
    assertThat(request.authUserId).isEqualTo(USER_ID);
    assertThat(request.param).isEqualTo(notMatchingUserId);
    // When
    final var controllerResponse = taskController.findByUserId(findTasksByUserIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(401);
    assertThat(controllerResponse.body.toString()).contains(UserNotResourceOwnerException.message);
  }

  @Test
  @DisplayName("Valid request but user not found => 400/message")
  void userNotFound() {
    final var mockUserDataAccess = UserDataAccessMock.getMockForUserNotFoundById(USER_ID);
    final var findTasksByUserIdUseCase = new FindTasksByUserIdUseCase(mockTaskDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    request.authUserId = USER_ID;
    request.param = USER_ID;
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.param).isEqualTo(USER_ID);
    // When
    final var controllerResponse = taskController.findByUserId(findTasksByUserIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserNotFoundByIdException.message);
  }

  @Test
  @DisplayName("Valid request, user found and tasks found => 200/tasks")
  void userAndTasksFound() {
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTasks = mockTaskDataAccess.findByUserId(USER_ID);
    request.authUserId = USER_ID;
    request.param = USER_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTasks).isNotNull().isNotEmpty();
    assertThat(request.param).isEqualTo(USER_ID);
    // When
    final var controllerResponse = taskController.findByUserId(findTasksByUserIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(200);
    final List<String> responseTaskIds = getResponseTaskIds((List<TaskDto>) controllerResponse.body);
    final List<String> tasksIds = getTasksIds(TASKS);
    assertThat(responseTaskIds).hasSameElementsAs(tasksIds);
  }

  private List<String> getResponseTaskIds(List<TaskDto> tasks) {
    return  tasks.stream()
      .map(task -> task.id)
      .collect(Collectors.toList());
  }

  private List<String> getTasksIds(List<Task> tasks) {
    return tasks.stream()
      .map(task -> task.getId())
      .collect(Collectors.toList());
  }

  @Test
  @DisplayName("Valid request and user found but not tasks found => 200/empty tasks")
  void userFoundButTasksNotFound() {
    final var mockTaskDataAccess = TaskDataAccessMock.getMockForTasksNotFoundByUserId(USER_ID);
    final var findTasksByUserIdUseCase = new FindTasksByUserIdUseCase(mockTaskDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTasks = mockTaskDataAccess.findByUserId(USER_ID);
    request.authUserId = USER_ID;
    request.param = USER_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTasks).isNotNull().isEmpty();
    assertThat(request.param).isEqualTo(USER_ID);
    // When
    final var controllerResponse = taskController.findByUserId(findTasksByUserIdUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(200);
    assertThat((List<TaskDto>) controllerResponse.body).isNotNull().isEmpty();
  }

}
