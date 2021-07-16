package com.pedrofrohmut.todos.controllers;

import com.pedrofrohmut.todos.dtos.CreateTaskDto;
import com.pedrofrohmut.todos.dtos.UpdateTaskDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/tasks")
public class TaskController {

  @PostMapping
  public ResponseEntity<?> create(@RequestBody CreateTaskDto dto) {
    return new ResponseEntity<>(dto, HttpStatus.CREATED);
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<?> findById(@PathVariable String taskId) {
    return new ResponseEntity<>(taskId, HttpStatus.OK);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<?> findTasksByUserId(@PathVariable String userId) {
    return new ResponseEntity<>(userId, HttpStatus.OK);
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<?> update(@RequestBody UpdateTaskDto dto) {
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<?> delete(@PathVariable String taskId) {
    return new ResponseEntity<>(taskId, HttpStatus.OK);
  }

}
