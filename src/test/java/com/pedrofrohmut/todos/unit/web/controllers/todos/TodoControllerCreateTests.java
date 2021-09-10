package com.pedrofrohmut.todos.unit.web.controllers.todos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.TodoDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.CreateTodoDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Todo;
import com.pedrofrohmut.todos.domain.errors.InvalidEntityException;
import com.pedrofrohmut.todos.domain.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.todos.CreateTodoUseCase;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.mocks.TaskDataAccessMock;
import com.pedrofrohmut.todos.mocks.UserDataAccessMock;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.controllers.TodoController;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Todo controller create method")
public class TodoControllerCreateTests {

  static final String USER_ID = UUID.randomUUID().toString();
  static final String USER_NAME = "User Name";
  static final String USER_EMAIL = "user@mail.com";
  static final String USER_PASSWORD = "user_password";

  static final String TASK_ID = UUID.randomUUID().toString();
  static final String TASK_NAME = "Task Name";
  static final String TASK_DESCRIPTION = "Task Description";

  static final String TODO_TITLE = "Todo Title";
  static final String TODO_DESCRIPTION = "Todo Description";

  final TodoController todoController;
  final PasswordService passwordService;

  public TodoControllerCreateTests() {
    passwordService = new BcryptPasswordService();
    todoController = new TodoController();
  }

  TodoDataAccess mockTodoDataAccess;
  TaskDataAccess mockTaskDataAccess;
  UserDataAccess mockUserDataAccess;
  AdaptedRequest<CreateTodoDto> request;
  CreateTodoUseCase createTodoUseCase;

  @BeforeEach
  void beforeEach() {
    mockTaskDataAccess = TaskDataAccessMock.getMockForTaskFoundById(TASK_ID, TASK_NAME, TASK_DESCRIPTION, USER_ID);
    mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundById(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    mockTodoDataAccess = mock(TodoDataAccess.class);
    createTodoUseCase = new CreateTodoUseCase(mockTodoDataAccess, mockTaskDataAccess, mockUserDataAccess);
    request = new AdaptedRequest<>(null, null, null);
  }

  @Test
  @DisplayName("Null body => 400/message")
  void nullBody() {
    // Given
    assertThat(request.body).isNull();
    // Then
    final var controllerResponse = todoController.create(createTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestBodyException.message);
  }

  @Test
  @DisplayName("Invalid body.title => 400/message")
  void invalidBodyTitle() {
    final var invalidTitle = "";
    final var titleErr = getTitleErr(invalidTitle);
    request.body = new CreateTodoDto(invalidTitle, TODO_DESCRIPTION, TASK_ID);
    // Given
    assertThat(titleErr).isNotNull();
    assertThat(request.body.title).isEqualTo(invalidTitle);
    // Then
    final var controllerResponse = todoController.create(createTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(titleErr.getMessage());
  }

  private Exception getTitleErr(String title) {
    try {
      Todo.validateTitle(title);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Invalid body.description => 400/message")
  void invalidBodyDescription() {
    final var invalidDescription = "Amet quam expedita fuga accusamus blanditiis cupiditate? Modi molestiae ullam hic odit asperiores Esse dignissimos reiciendis? Lorem quidem nesciunt asperiores impedit debitis? Incidunt in quod maiores adipisci esse quidem! Voluptatum corrupti culpa. Adipisicing et quidem eaque animi impedit. Amet delectus provident nisi quis.";
    final var descriptionErr = getDescriptionErr(invalidDescription);
    request.body = new CreateTodoDto(TODO_TITLE, invalidDescription, TASK_ID);
    // Given
    assertThat(descriptionErr).isNotNull();
    assertThat(request.body.description).isEqualTo(invalidDescription);
    // When
    final var controllerResponse = todoController.create(createTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(descriptionErr.getMessage());
  }

  private Exception getDescriptionErr(String description) {
    try {
      Todo.validateDescription(description);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Invalid body.taskId => 400/message")
  void invalidBodyTaskId() {
    final var invalidTaskId = "";
    final var taskIdErr = getTaskIdErr(invalidTaskId);
    request.body = new CreateTodoDto(TODO_TITLE, TODO_DESCRIPTION, invalidTaskId);
    // Given
    assertThat(taskIdErr).isNotNull();
    assertThat(request.body.taskId).isEqualTo(invalidTaskId);
    // When
    final var controllerResponse = todoController.create(createTodoUseCase, request);
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
  @DisplayName("Null authUserId => 401/message")
  void nullAuthUserId() {
    request.body = new CreateTodoDto(TODO_TITLE, TODO_DESCRIPTION, TASK_ID);
    // Given
    assertThat(request.authUserId).isNull();
    // When
    final var controllerResponse = todoController.create(createTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(401);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestAuthUserIdException.message);
  }

  @Test
  @DisplayName("Invalid authUserId => 400/message")
  void invalidAuthUserId() {
    final var invalidAuthUserId = "";
    final var authUserIdErr = getAuthUserIdErr(invalidAuthUserId);
    request.body = new CreateTodoDto(TODO_TITLE, TODO_DESCRIPTION, TASK_ID);
    request.authUserId = invalidAuthUserId;
    // Given
    assertThat(authUserIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.authUserId).isEqualTo(invalidAuthUserId);
    // When
    final var controllerResponse = todoController.create(createTodoUseCase, request);
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
  @DisplayName("Valid request but user not found by request.authUserId => 400/message")
  void userNotFound() {
    final var mockUserDataAccess = UserDataAccessMock.getMockForUserNotFoundById(USER_ID);
    final var createTodoUseCase = new CreateTodoUseCase(mockTodoDataAccess, mockTaskDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    request.body = new CreateTodoDto(TODO_TITLE, TODO_DESCRIPTION, TASK_ID);
    request.authUserId = USER_ID;
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.authUserId).isEqualTo(USER_ID);
    // When
    final var controllerResponse = todoController.create(createTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserNotFoundByIdException.message);
    verify(mockUserDataAccess, times(2)).findById(USER_ID);
    verify(mockTaskDataAccess, times(0)).findById(TASK_ID);
    verify(mockTodoDataAccess, times(0)).create(any(Todo.class));
  }

  @Test
  @DisplayName("Valid request and user found but task not found by request.body.taskId => 400/message")
  void userFoundButTaskNotFound() {
    final var mockTaskDataAccess = TaskDataAccessMock.getMockForTaskNotFoundById(TASK_ID);
    final var createTodoUseCase = new CreateTodoUseCase(mockTodoDataAccess, mockTaskDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTask = mockTaskDataAccess.findById(TASK_ID);
    request.body = new CreateTodoDto(TODO_TITLE, TODO_DESCRIPTION, TASK_ID);
    request.authUserId = USER_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTask).isNull();
    assertThat(request.body.taskId).isEqualTo(TASK_ID);
    // When
    final var controllerResponse = todoController.create(createTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(TaskNotFoundByIdException.message);
    verify(mockUserDataAccess, times(2)).findById(USER_ID);
    verify(mockTaskDataAccess, times(2)).findById(TASK_ID);
    verify(mockTodoDataAccess, times(0)).create(any(Todo.class));
  }

  @Test
  @DisplayName("Valid request, user and task found => 201")
  void userAndTaskFound() {
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTask = mockTaskDataAccess.findById(TASK_ID);
    final var titleErr = getTitleErr(TODO_TITLE);
    final var descriptionErr = getDescriptionErr(TODO_DESCRIPTION);
    final var taskIdErr = getTaskIdErr(TASK_ID);
    final var authUserIdErr = getAuthUserIdErr(USER_ID);
    request.body = new CreateTodoDto(TODO_TITLE, TODO_DESCRIPTION, TASK_ID);
    request.authUserId = USER_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTask).isNotNull();
    assertThat(titleErr).isNull();
    assertThat(descriptionErr).isNull();
    assertThat(authUserIdErr).isNull();
    assertThat(taskIdErr).isNull();
    assertThat(request.body.title).isEqualTo(TODO_TITLE);
    assertThat(request.body.description).isEqualTo(TODO_DESCRIPTION);
    assertThat(request.body.taskId).isEqualTo(TASK_ID);
    assertThat(request.authUserId).isEqualTo(USER_ID);
    // When
    final var controllerResponse = todoController.create(createTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(201);
    assertThat(controllerResponse.body).isNull();
    verify(mockUserDataAccess, times(2)).findById(USER_ID);
    verify(mockTaskDataAccess, times(2)).findById(TASK_ID);
    verify(mockTodoDataAccess, times(1)).create(any(Todo.class));
  }

}
