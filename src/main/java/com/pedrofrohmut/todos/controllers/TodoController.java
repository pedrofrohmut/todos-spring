package com.pedrofrohmut.todos.controllers;

import com.pedrofrohmut.todos.dtos.CreateTodoDto;
import com.pedrofrohmut.todos.dtos.UpdateTodoDto;
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
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/{todoId}")
  public ResponseEntity<?> findOneById(@PathVariable String todoId) {
    return new ResponseEntity<>(todoId, HttpStatus.OK);
  }

  @GetMapping("/task/{taskId}")
  public ResponseEntity<?> findAllByTaskId(@PathVariable String taskId) {
    return new ResponseEntity<>(taskId, HttpStatus.OK);
  }

  @PutMapping("/{todoId}")
  public ResponseEntity<?> update(@PathVariable String todoId, @RequestBody UpdateTodoDto dto) {
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @PatchMapping("/setdone/{todoId}")
  public ResponseEntity<?> setDone(@PathVariable String todoId) {
    return new ResponseEntity<>(todoId, HttpStatus.OK);
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
