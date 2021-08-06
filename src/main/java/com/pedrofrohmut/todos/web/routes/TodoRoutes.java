package com.pedrofrohmut.todos.web.routes;

import com.pedrofrohmut.todos.domain.dtos.CreateTodoDto;
import com.pedrofrohmut.todos.domain.dtos.UpdateTodoDto;
import com.pedrofrohmut.todos.web.adapter.SpringAdapter;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping("/api/todos")
public class TodoRoutes {

  private static final String TOKEN_HEADER = "authentication_token";

  @PostMapping
  public ResponseEntity<?> create(
      @RequestBody CreateTodoDto dto, @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController("TodoController", "create", dto, token, null);
  }

  @GetMapping("/{todoId}")
  public ResponseEntity<?> findById(
      @PathVariable String todoId, @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController("TodoController", "findById", null, token, todoId);
  }

  @GetMapping("/task/{taskId}")
  public ResponseEntity<?> findByTaskId(
      @PathVariable String taskId, @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController("TodoController", "findByTaskId", null, token, taskId);
  }

  @PutMapping("/{todoId}")
  public ResponseEntity<?> update(
      @PathVariable String todoId,
      @RequestBody UpdateTodoDto dto,
      @RequestHeader(TOKEN_HEADER) String token
  ) {
    return SpringAdapter.callController("TodoController", "update", dto, token, todoId);
  }

  @PatchMapping("/setdone/{todoId}")
  public ResponseEntity<?> setDone(
      @PathVariable String todoId, @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController("TodoController", "setDone", null, token, todoId);
  }

  @PatchMapping("/setnotdone/{todoId}")
  public ResponseEntity<?> setNotDone(
      @PathVariable String todoId, @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController("TodoController", "setNotDone", null, token, todoId);
  }

  @DeleteMapping("/{todoId}")
  public ResponseEntity<?> delete(
      @PathVariable String todoId, @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController("TodoController", "delete", null, token, todoId);
  }

  @DeleteMapping("/task/{taskId}")
  public ResponseEntity<?> clearCompleteByTaskId(
      @PathVariable String taskId, @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController(
        "TodoController", "clearCompleteByTaskId", null, token, taskId);
  }

}
