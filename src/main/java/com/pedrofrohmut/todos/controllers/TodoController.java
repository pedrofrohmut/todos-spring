package com.pedrofrohmut.todos.controllers;

import com.pedrofrohmut.todos.dtos.CreateTodoDto;
import com.pedrofrohmut.todos.dtos.UpdateTodoDto;
import com.pedrofrohmut.todos.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.errors.TodoNotFoundByIdException;
import com.pedrofrohmut.todos.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.repositories.TaskRepository;
import com.pedrofrohmut.todos.repositories.TodoRepository;
import com.pedrofrohmut.todos.repositories.UserRepository;
import com.pedrofrohmut.todos.services.JwtService;
import com.pedrofrohmut.todos.services.TodoService;
import com.pedrofrohmut.todos.utils.ConnectionFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/todos")
public class TodoController {

  private static final String TOKEN_HEADER = "authentication_token";

  @PostMapping
  public ResponseEntity<?> create(
      @RequestBody CreateTodoDto dto, @RequestHeader(TOKEN_HEADER) String token) {
    try {
      final var authUserId = getUserIdFromToken(token);
      final var todoService = createTodoService();
      todoService.create(dto, authUserId);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UserNotFoundByIdException | UserNotResourceOwnerException | TaskNotFoundByIdException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{todoId}")
  public ResponseEntity<?> findById(
      @PathVariable String todoId, @RequestHeader(TOKEN_HEADER) String token) {
    try {
      final var authUserId = getUserIdFromToken(token);
      final var todoService = createTodoService();
      final var foundTodo = todoService.findById(todoId, authUserId);
      return new ResponseEntity<>(foundTodo, HttpStatus.OK);
    } catch (UserNotFoundByIdException | UserNotResourceOwnerException | TodoNotFoundByIdException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/task/{taskId}")
  public ResponseEntity<?> findByTaskId(
      @PathVariable String taskId, @RequestHeader(TOKEN_HEADER) String token) {
    try {
      final var authUserId = getUserIdFromToken(token);
      final var todoService = createTodoService();
      final var tasks = todoService.findByTaskId(taskId, authUserId);
      return new ResponseEntity<>(tasks, HttpStatus.OK);
    } catch (UserNotFoundByIdException | UserNotResourceOwnerException | TaskNotFoundByIdException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/{todoId}")
  public ResponseEntity<?> update(
      @PathVariable String todoId,
      @RequestBody UpdateTodoDto dto,
      @RequestHeader(TOKEN_HEADER) String token
  ) {
    try {
      final var authUserId = getUserIdFromToken(token);
      final var todoService = createTodoService();
      todoService.update(todoId, dto, authUserId);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (UserNotFoundByIdException | UserNotResourceOwnerException | TodoNotFoundByIdException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping("/setdone/{todoId}")
  public ResponseEntity<?> setDone(
      @PathVariable String todoId, @RequestHeader(TOKEN_HEADER) String token) {
    try {
      final var authUserId = getUserIdFromToken(token);
      final var todoService = createTodoService();
      todoService.setDone(todoId, authUserId);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping("/setnotdone/{todoId}")
  public ResponseEntity<?> setNotDone(@PathVariable String todoId) {
    return new ResponseEntity<>(todoId, HttpStatus.OK);
  }

  @DeleteMapping("/{todoId}")
  public ResponseEntity<?> delete(@PathVariable String todoId) {
    return new ResponseEntity<>(todoId, HttpStatus.OK);
  }

  @DeleteMapping("/task/{taskId}")
  public ResponseEntity<?> clearAllCompleteByTaskId(@PathVariable String taskId) {
    return new ResponseEntity<>(taskId, HttpStatus.OK);
  }

  private TodoService createTodoService() {
    final var connectionFactory = new ConnectionFactory();
    final var connection = connectionFactory.getConnection();
    final var userRepository = new UserRepository(connection);
    final var taskRepository = new TaskRepository(connection);
    final var todoRepository = new TodoRepository(connection);
    return new TodoService(userRepository, taskRepository, todoRepository);
  }

  private String getUserIdFromToken(String token) {
    final var jwtService = new JwtService();
    final var authUserId = jwtService.getUserIdFromToken(token);
    return authUserId;
  }

}
