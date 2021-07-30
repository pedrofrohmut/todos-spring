package com.pedrofrohmut.todos.services;

import java.util.List;

import com.pedrofrohmut.todos.dtos.CreateTodoDto;
import com.pedrofrohmut.todos.dtos.TodoDto;
import com.pedrofrohmut.todos.dtos.UpdateTodoDto;
import com.pedrofrohmut.todos.errors.TaskNotFoundByIdException;
import com.pedrofrohmut.todos.errors.TodoNotFoundByIdException;
import com.pedrofrohmut.todos.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.errors.UserNotResourceOwnerException;
import com.pedrofrohmut.todos.repositories.TaskRepository;
import com.pedrofrohmut.todos.repositories.TodoRepository;
import com.pedrofrohmut.todos.repositories.UserRepository;

public class TodoService {

  private static final String errorMessage = "[TodoService] %s";

  private final UserRepository userRepository;
  private final TaskRepository taskRepository;
  private final TodoRepository todoRepository;

  public TodoService(
      UserRepository userRepository,
      TaskRepository taskRepository,
      TodoRepository todoRepository) {
    this.userRepository = userRepository;
    this.taskRepository = taskRepository;
    this.todoRepository = todoRepository;
  }

  public void create(CreateTodoDto dto, String authUserId) {
    final var foundUser = this.userRepository.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoService.errorMessage, "create"));
    }
    final var foundTask = this.taskRepository.findById(dto.taskId);
    if (foundTask == null) {
      throw new TaskNotFoundByIdException(String.format(TodoService.errorMessage, "create"));
    }
    if (!foundTask.userId.equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoService.errorMessage, "create"));
    }
    final var todoToCreateDto = readyDtoToCreate(dto, authUserId);
    this.todoRepository.create(todoToCreateDto);
  }

  private CreateTodoDto readyDtoToCreate(CreateTodoDto dto, String authUserId) {
    final var todo = new CreateTodoDto();
    todo.name = dto.name;
    todo.description = dto.description == null ? "" : dto.description;
    todo.isDone = false;
    todo.taskId = dto.taskId;
    todo.userId = authUserId;
    return todo;
  }

  public TodoDto findById(String todoId, String authUserId) {
    final var foundUser = this.userRepository.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoService.errorMessage, "findById"));
    }
    final var foundTodo = this.todoRepository.findById(todoId);
    if (foundTodo == null) {
      throw new TodoNotFoundByIdException(String.format(TodoService.errorMessage, "findById"));
    }
    if (!foundTodo.userId.equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoService.errorMessage, "findById"));
    }
    return foundTodo;
  }

  public List<TodoDto> findByTaskId(String taskId, String authUserId) {
    final var foundUser = this.userRepository.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoService.errorMessage, "findByTaskId"));
    }
    final var foundTask = this.taskRepository.findById(taskId);
    if (foundTask == null) {
      throw new TaskNotFoundByIdException(String.format(TodoService.errorMessage, "findByTaskId"));
    }
    if (!foundTask.userId.equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoService.errorMessage, "findByTaskId"));
    }
    final var tasks = this.todoRepository.findByTaskId(taskId);
    return tasks;
  }

  public void update(String todoId, UpdateTodoDto dto, String authUserId) {
    final var foundUser = this.userRepository.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoService.errorMessage, "update"));
    }
    final var foundTodo = this.todoRepository.findById(todoId);
    if (foundTodo == null) {
      throw new TodoNotFoundByIdException(String.format(TodoService.errorMessage, "update"));
    }
    if (!foundTodo.userId.equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoService.errorMessage, "update"));
    }
    final var updatedTodoDto = readyDtoToUpdate(todoId,  dto);
    this.todoRepository.update(updatedTodoDto);
  }

  private UpdateTodoDto readyDtoToUpdate(String todoId, UpdateTodoDto dto) {
    final var todo = new UpdateTodoDto();
    todo.id = todoId;
    todo.name = dto.name;
    todo.description = dto.description == null ? "" : dto.description;
    return todo;
  }

  public void setDone(String todoId, String authUserId) {
    final var foundUser = this.userRepository.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoService.errorMessage, "setDone"));
    }
    final var foundTodo = this.todoRepository.findById(todoId);
    if (foundTodo == null) {
      throw new TodoNotFoundByIdException(String.format(TodoService.errorMessage, "setDone"));
    }
    if (!foundTodo.userId.equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoService.errorMessage, "setDone"));
    }
    this.todoRepository.setDone(todoId);
  }

  public void setNotDone(String todoId, String authUserId) {
    final var foundUser = this.userRepository.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(TodoService.errorMessage, "setNotDone"));
    }
    final var foundTodo = this.todoRepository.findById(todoId);
    if (foundTodo == null) {
      throw new TodoNotFoundByIdException(String.format(TodoService.errorMessage, "setNotDone"));
    }
    if (!foundTodo.userId.equals(authUserId)) {
      throw new UserNotResourceOwnerException(String.format(TodoService.errorMessage, "setNotDone"));
    }
    this.todoRepository.setNotDone(todoId);
  }
}
