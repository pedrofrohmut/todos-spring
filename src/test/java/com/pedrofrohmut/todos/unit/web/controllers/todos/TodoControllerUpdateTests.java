package com.pedrofrohmut.todos.unit.web.controllers.todos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.TodoDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.UpdateTodoDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Todo;
import com.pedrofrohmut.todos.domain.errors.InvalidEntityException;
import com.pedrofrohmut.todos.domain.errors.TodoNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.domain.usecases.todos.UpdateTodoUseCase;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.mocks.TodoDataAccessMock;
import com.pedrofrohmut.todos.mocks.UserDataAccessMock;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.controllers.TodoController;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;
import com.pedrofrohmut.todos.web.errors.MissingRequestParametersException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Todo controller update")
public class TodoControllerUpdateTests {

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
  final UpdateTodoUseCase updateTodoUseCase;
  final TodoController todoController;

  public TodoControllerUpdateTests() {
    passwordService = new BcryptPasswordService();
    mockTodoDataAccess =
      TodoDataAccessMock.getMockForTodoFoundById(TODO_ID, TODO_TITLE, TODO_DESCRIPTION, TODO_IS_DONE, TASK_ID, USER_ID);
    mockUserDataAccess =
      UserDataAccessMock.getMockForUserFoundById(USER_ID, USER_NAME, USER_EMAIL, USER_PASSWORD, passwordService);
    updateTodoUseCase = new UpdateTodoUseCase(mockTodoDataAccess, mockUserDataAccess);
    todoController = new TodoController();
  }

  AdaptedRequest<UpdateTodoDto> request;

  @BeforeEach
  void beforeEach() {
    request = new AdaptedRequest<>(null, null, null);
  }


  @Test
  @DisplayName("Null body => 400/message")
  void nullBody() {
    request.param = TODO_ID;
    request.authUserId = USER_ID;
    // Given
    assertThat(request.body).isNull();
    // Then
    final var controllerResponse = todoController.update(updateTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestBodyException.message);
  }

  @Test
  @DisplayName("Invalid body.title => 400/message")
  void invalidBodyTitle() {
    final var invalidTitle = "";
    final var titleErr = getTitleErr(invalidTitle);
    request.param = TODO_ID;
    request.authUserId = USER_ID;
    request.body = new UpdateTodoDto(invalidTitle, TODO_DESCRIPTION);
    // Given
    assertThat(titleErr).isNotNull();
    assertThat(request.body.title).isEqualTo(invalidTitle);
    // Then
    final var controllerResponse = todoController.update(updateTodoUseCase, request);
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
    request.body = new UpdateTodoDto(TODO_TITLE, invalidDescription);
    request.authUserId = USER_ID;
    request.param = TODO_ID;
    // Given
    assertThat(descriptionErr).isNotNull();
    assertThat(request.body.description).isEqualTo(invalidDescription);
    // When
    final var controllerResponse = todoController.update(updateTodoUseCase, request);
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
  @DisplayName("Null authUser => 401/message")
  void nullAuthUserId() {
    request.param = TODO_ID;
    request.body = new UpdateTodoDto(TODO_TITLE, TODO_DESCRIPTION);
    // Given
    assertThat(request.authUserId).isNull();
    // When
    final var controllerResponse = todoController.update(updateTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(401);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestAuthUserIdException.message);
  }

  @Test
  @DisplayName("Invalid authUserId => 400/message")
  void invalidAuthUserId() {
    final var invalidAuthUserId = "";
    Exception authUserIdErr = getAuthUserIdErr(invalidAuthUserId);
    request.param = TODO_ID;
    request.authUserId = invalidAuthUserId;
    request.body = new UpdateTodoDto(TODO_TITLE, TODO_DESCRIPTION);
    // Given
    assertThat(authUserIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.authUserId).isEqualTo(invalidAuthUserId);
    // When
    final var controllerResponse = todoController.update(updateTodoUseCase, request);
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
    request.body = new UpdateTodoDto(TODO_TITLE, TODO_DESCRIPTION);
    // Given
    assertThat(request.param).isNull();
    // When
    final var controllerResponse = todoController.update(updateTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestParametersException.message);
  }

  @Test
  @DisplayName("Invalid request.param for todoId => 400/message")
  void invalidRequestParam() {
    final var invalidTodoId = "";
    final var todoIdErr = getTodoIdErr(invalidTodoId);
    request.body = new UpdateTodoDto(TODO_TITLE, TODO_DESCRIPTION);
    request.authUserId = USER_ID;
    request.param = invalidTodoId;
    // Given
    assertThat(todoIdErr).isNotNull().isInstanceOf(InvalidEntityException.class);
    assertThat(request.param).isEqualTo(invalidTodoId);
    // When
    final var controllerResponse = todoController.update(updateTodoUseCase, request);
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
  @DisplayName("Valid request but user not found by request.authUserId => 400/message")
  void userNotFound() {
    final var mockUserDataAccess = UserDataAccessMock.getMockForUserNotFoundById(USER_ID);
    final var updateTodoUseCase = new UpdateTodoUseCase(mockTodoDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    request.body = new UpdateTodoDto(TODO_TITLE, TODO_DESCRIPTION);
    request.authUserId = USER_ID;
    request.param = TODO_ID;
    // Given
    assertThat(foundUser).isNull();
    assertThat(request.authUserId).isEqualTo(USER_ID);
    // When
    final var controllerResponse = todoController.update(updateTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(UserNotFoundByIdException.message);
  }


  @Test
  @DisplayName("Valid request and user found but todo not found => 400/message")
  void todoNotFound() {
    final var mockTodoDataAccess = TodoDataAccessMock.getMockForTodoNotFoundById(TODO_ID);
    final var updateTodoUseCase = new UpdateTodoUseCase(mockTodoDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTodo = mockTodoDataAccess.findById(TODO_ID);
    request.body = new UpdateTodoDto(TODO_TITLE, TODO_DESCRIPTION);
    request.authUserId = USER_ID;
    request.param = TODO_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTodo).isNull();
    assertThat(request.param).isEqualTo(TODO_ID);
    // When
    final var controllerResponse = todoController.update(updateTodoUseCase, request);
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
    final var updateTodoUseCase = new UpdateTodoUseCase(mockTodoDataAccess, mockUserDataAccess);
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTodo = mockTodoDataAccess.findById(otherUserTodoId);
    request.body = new UpdateTodoDto(TODO_TITLE, TODO_DESCRIPTION);
    request.authUserId = USER_ID;
    request.param = otherUserTodoId;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTodo).isNotNull();
    assertThat(foundTodo.getUserId()).isNotEqualTo(USER_ID);
    assertThat(request.authUserId).isEqualTo(USER_ID);
    assertThat(request.param).isEqualTo(otherUserTodoId);
    // When
    final var controllerResponse = todoController.update(updateTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(401);
    assertThat(controllerResponse.body.toString()).contains(UserNotResourceOwnerException.message);
  }

  @Test
  @DisplayName("Valid request, user and todo found => 204")
  void userAndTodoFound() {
    final var foundUser = mockUserDataAccess.findById(USER_ID);
    final var foundTodo = mockTodoDataAccess.findById(TODO_ID);
    request.body = new UpdateTodoDto(TODO_TITLE, TODO_DESCRIPTION);
    request.authUserId = USER_ID;
    request.param = TODO_ID;
    // Given
    assertThat(foundUser).isNotNull();
    assertThat(foundTodo).isNotNull();
    assertThat(request.authUserId).isEqualTo(USER_ID);
    assertThat(request.param).isEqualTo(TODO_ID);
    // When
    final var controllerResponse = todoController.update(updateTodoUseCase, request);
    // Then
    assertThat(controllerResponse.httpStatus).isEqualTo(204);
    assertThat(controllerResponse.body).isNull();
    verify(mockTodoDataAccess, times(1)).update(any(Todo.class));
  }

}
