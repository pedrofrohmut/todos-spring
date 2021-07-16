package com.pedrofrohmut.todos.entities;

import java.util.Collection;

public class Task {

  private final String id;
  private final String name;
  private final String description;

  private Collection<Todo> todos;

  public Task(String id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public Task(String name, String description) {
    this.id = "";
    this.name = name;
    this.description = description;
  }

  public String getId() { return id; }

  public String getName() { return name; }

  public String getDescription() { return description; }

  public Collection<Todo> getTodos() { return todos; }

  public void setTodos(Collection<Todo> todos) { this.todos = todos; }

}
