package com.pedrofrohmut.todos.domain.entities;

import com.pedrofrohmut.todos.domain.errors.InvalidTodoException;

public class Todo extends Entity {

  private final String id;
  private final String title;
  private final String description;
  private final boolean isDone;

  private final String taskId;
  private final String userId;

  public Todo(
      String id, String title, String description, boolean isDone, String taskId, String userId) {
    this.validateId(id);
    this.validateTitle(title);
    this.validateDescription(description);
    this.validateId(taskId);
    this.validateId(userId);
    this.id = id;
    this.title = title;
    this.description = description;
    this.isDone = isDone;
    this.taskId = taskId;
    this.userId = userId;
  }

  public Todo(String title, String description, String taskId, String userId) {
    this.validateTitle(title);
    this.validateDescription(description);
    this.validateId(taskId);
    this.validateId(userId);
    this.id = "";
    this.title = title;
    this.description = description;
    this.isDone = false;
    this.taskId = taskId;
    this.userId = userId;
  }

  private void validateTitle(String title) {
    if (title.isBlank()) {
      throw new InvalidTodoException("Title is required an cannot be blank");
    }
    if (title.length() < 3 || title.length() > 64) {
      throw new InvalidTodoException("Title must be between 3 and 64 characters");
    }
  }

  private void validateDescription(String description) {
    if (description.length() > 255) {
      throw new InvalidTodoException("Description must be less than 255 characters");
    }
  }

  public String getId() { return id; }

  public String getTitle() { return title; }

  public String getDescription() { return description; }

  public boolean isDone() { return isDone; }

  public String getTaskId() { return taskId; }

  public String getUserId() { return userId; }

}
