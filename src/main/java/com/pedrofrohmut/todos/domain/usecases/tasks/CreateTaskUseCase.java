package com.pedrofrohmut.todos.domain.usecases.tasks;

import com.pedrofrohmut.todos.domain.dataaccess.TaskDataAccess;
import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.CreateTaskDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.Task;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

public class CreateTaskUseCase {

  private static final String errorMessage = "CreateTaskUseCase execute";

  private final TaskDataAccess taskDataAccess;
  private final UserDataAccess userDataAccess;

  public CreateTaskUseCase(TaskDataAccess taskDataAccess, UserDataAccess userDataAccess) {
    this.taskDataAccess = taskDataAccess;
    this.userDataAccess = userDataAccess;
  }

  public void execute(CreateTaskDto newTask, String authUserId) {
    checkNewTask(newTask);
    checkAuthUserId(authUserId);
    checkUserExists(authUserId);
    createTask(newTask, authUserId);
  }

  private void checkNewTask(CreateTaskDto newTask) {
    if (newTask == null) {
      throw new MissingRequestBodyException(errorMessage);
    }
    Task.validateName(newTask.name);
    Task.validateDescription(newTask.description);
  }

  private void checkAuthUserId(String authUserId) {
    if (authUserId == null) {
      throw new MissingRequestAuthUserIdException(errorMessage);
    }
    Entity.validateId(authUserId);
  }

  private void checkUserExists(String userId) {
    final var foundUser = userDataAccess.findById(userId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(errorMessage);
    }
  }

  private void createTask(CreateTaskDto taskDto, String authUserId) {
    final var newTask = new Task(taskDto.name, taskDto.description, authUserId);
    taskDataAccess.create(newTask);
  }

}
