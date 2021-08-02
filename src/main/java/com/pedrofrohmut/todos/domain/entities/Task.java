package com.pedrofrohmut.todos.domain.entities;

import java.util.List;

import com.pedrofrohmut.todos.domain.errors.InvalidTaskException;

public class Task extends Entity {

  private final String id;
  private final String name;
  private final String description;

  private final String userId;
  private List<Todo> todos;

  public Task(String id, String name, String description, String userId) {
    this.validateId(id);
    this.validateName(name);
    this.validateDescription(description);
    this.validateId(userId);
    this.id = id;
    this.name = name;
    this.description = description;
    this.userId = userId;
  }

  public Task(String name, String description, String userId) {
    this.validateName(name);
    this.validateDescription(description);
    this.validateId(userId);
    this.id = "";
    this.name = name;
    this.description = description;
    this.userId = userId;
  }

  private void validateName(String name) {
    if (name.isBlank()) {
      throw new InvalidTaskException("Name is required and cannot be blank");
    }
    if (name.length() > 64 || name.length() < 3) {
      throw new InvalidTaskException("Name must be between 3 and 64 characters");
    }
  }

  private void validateDescription(String description) {
    if (description.length() > 255) {
      throw new InvalidTaskException("Description must be less than 255 characters");
    }
  }

  private void validateTodos(List<Todo> todos) {
    todos.forEach(todo -> {
      if (!todo.getUserId().equals(userId)) {
        throw new InvalidTaskException("Todo do not belong to the user of this task");
      }
      if (!todo.getTaskId().equals(id)) {
        throw new InvalidTaskException("Todo do not belong to this task");
      }
    });
  }

  public String getId() { return id; }

  public String getName() { return name; }

  public String getDescription() { return description; }

  public String getUserId() { return userId; }

  public List<Todo> getTodos() { return todos; }

  public void setTodos(List<Todo> todos) {
    this.validateTodos(todos);
    this.todos = todos;
  }

}
