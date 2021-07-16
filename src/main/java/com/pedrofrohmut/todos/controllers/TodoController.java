package com.pedrofrohmut.todos.controllers;

import com.pedrofrohmut.todos.dtos.CreateTodoDto;
import com.pedrofrohmut.todos.dtos.UpdateTodoDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/todos")
public class TodoController {

  @PostMapping
  public ResponseEntity<?> create(@RequestBody CreateTodoDto dto) {
    return new ResponseEntity<>(dto, HttpStatus.OK);
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
    var tuple = new Object() {
      public final String id = todoId;
      public final UpdateTodoDto body = dto;
    };
    return new ResponseEntity<>(tuple, HttpStatus.OK);
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

}
