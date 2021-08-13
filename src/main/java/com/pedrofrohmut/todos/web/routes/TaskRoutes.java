package com.pedrofrohmut.todos.web.routes;

import com.pedrofrohmut.todos.domain.dtos.CreateTaskDto;
import com.pedrofrohmut.todos.domain.dtos.UpdateTaskDto;
import com.pedrofrohmut.todos.web.adapter.SpringAdapter;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping("/api/tasks")
public class TaskRoutes {

  private static final String TOKEN_HEADER = "authentication_token";

  @PostMapping
  public ResponseEntity<?> create(
      @RequestBody CreateTaskDto dto, @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController("TaskController", "create", dto, token, null);
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<?> findById(
      @PathVariable String taskId, @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController("TaskController", "findById", null, token, taskId);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<?> findByUserId(
      @PathVariable String userId, @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController("TaskController", "findByUserId", null, token, userId);
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<?> update(
      @PathVariable String taskId,
      @RequestBody UpdateTaskDto dto,
      @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController("TaskController", "update", dto, token, taskId);
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<?> delete(
      @PathVariable String taskId, @RequestHeader(TOKEN_HEADER) String token) {
    return SpringAdapter.callController("TaskController", "delete", null, token, taskId);
  }

}
