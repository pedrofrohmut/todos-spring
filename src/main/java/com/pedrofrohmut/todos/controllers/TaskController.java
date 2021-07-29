package com.pedrofrohmut.todos.controllers;

import com.pedrofrohmut.todos.dtos.CreateTaskDto;
import com.pedrofrohmut.todos.dtos.UpdateTaskDto;
import com.pedrofrohmut.todos.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.repositories.TaskRepository;
import com.pedrofrohmut.todos.repositories.UserRepository;
import com.pedrofrohmut.todos.services.JwtService;
import com.pedrofrohmut.todos.services.TaskService;
import com.pedrofrohmut.todos.utils.ConnectionFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

  private static final String TOKEN_HEADER = "authentication_token";

  @PostMapping
  public ResponseEntity<?> create(
      @RequestBody CreateTaskDto dto, @RequestHeader(TOKEN_HEADER) String token) {
    try {
      final var authUserId = getUserIdFromToken(token);
      final var taskService = createTaskService();
      taskService.create(dto, authUserId);
      return new ResponseEntity<>(dto, HttpStatus.CREATED);
    } catch (UserNotFoundByIdException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private TaskService createTaskService() {
    final var connectionFactory = new ConnectionFactory();
    final var connection = connectionFactory.getConnection();
    final var userRepository = new UserRepository(connection);
    final var taskRepository = new TaskRepository(connection);
    final var taskService = new TaskService(userRepository, taskRepository);
    return taskService;
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<?> findById(
      @PathVariable String taskId, @RequestHeader(TOKEN_HEADER) String token) {
    try {
      final var authUserId = getUserIdFromToken(token);
      final var taskService = createTaskService();
      final var foundTask = taskService.findById(taskId, authUserId);
      return new ResponseEntity<>(foundTask, HttpStatus.OK);
    } catch (UserNotFoundByIdException | TaskNotFoundByIdException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  private String getUserIdFromToken(String token) {
    final var jwtService = new JwtService();
    final var authUserId = jwtService.getUserIdFromToken(token);
    return authUserId;
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<?> findTasksByUserId(
      @PathVariable String userId, @RequestHeader(TOKEN_HEADER) String token) {
    try {
      final var authUserId = getUserIdFromToken(token);
      final var taskService = createTaskService();
      final var tasks = taskService.findByUserId(userId, authUserId);
      return new ResponseEntity<>(tasks, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<?> update(
      @PathVariable String taskId,
      @RequestBody UpdateTaskDto dto,
      @RequestHeader(TOKEN_HEADER) String token
  ) {
    try {
      final var authUserId = getUserIdFromToken(token);
      final var taskService = createTaskService();
      taskService.update(taskId, dto, authUserId);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (UserNotFoundByIdException | TaskNotFoundByIdException | UserNotResourceOwnerException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<?> delete(
      @PathVariable String taskId, @RequestHeader(TOKEN_HEADER) String token) {
    try {
      final var authUserId = getUserIdFromToken(token);
      final var taskService = createTaskService();
      taskService.delete(taskId, authUserId);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
