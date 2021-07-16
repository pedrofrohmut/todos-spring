package com.pedrofrohmut.todos.entities;

public class Todo {

  private final String id;
  private final String title;
  private final String description;
  private final boolean isDone;

  public Todo(String id, String title, String description, boolean isDone) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.isDone = isDone;
  }

  public Todo(String title, String description) {
    this.id = "";
    this.title = title;
    this.description = description;
    this.isDone = false;
  }

  public String getId() { return id; }

  public String getTitle() { return title; }

  public String getDescription() { return description; }

  public boolean isDone() { return isDone; }

}
