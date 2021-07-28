package com.pedrofrohmut.todos.services;

import com.pedrofrohmut.todos.dtos.CreateTaskDto;
import com.pedrofrohmut.todos.dtos.TaskDto;
import com.pedrofrohmut.todos.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.repositories.TaskRepository;
import com.pedrofrohmut.todos.repositories.UserRepository;

public class TaskService {

  private static final String errorMessage = "[TaskService] %s";

  private final UserRepository userRepository;
  private final TaskRepository taskRepository;

  public TaskService(UserRepository userRepository, TaskRepository taskRepository) {
    this.userRepository = userRepository;
    this.taskRepository = taskRepository;
  }

  public void create(CreateTaskDto dto, String authUserId) {
    final var foundUser = this.userRepository.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TaskService.errorMessage, "create"));
    }
    readyDtoToCreate(dto, authUserId);
    this.taskRepository.create(dto);
  }

  private void readyDtoToCreate(CreateTaskDto dto, String authUserId) {
    if (dto.description == null) {
      dto.description = "";
    }
    dto.userId = authUserId;
  }

  public TaskDto findById(String taskId, String authUserId) {
    final var foundUser = this.userRepository.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TaskService.errorMessage, "findById"));
    }
    final var foundTask = this.taskRepository.findById(taskId);
    if (foundTask == null) {
      throw new TaskNotFoundByIdException(String.format(TaskService.errorMessage, "findById"));
    }
    return foundTask;
  }

}
