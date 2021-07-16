package com.pedrofrohmut.todos.entities;

import java.util.Collection;

public class User {

  private final String id;
  private final String name;
  private final String email;
  private final String password;

  private Collection<Task> tasks;

  public User(String id, String name, String email, String password) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
  }

  public User(String name, String email, String password) {
    this.id = "";
    this.name = name;
    this.email = email;
    this.password = password;
  }

  public User(String email, String password) {
    this.id = "";
    this.name = "";
    this.email = email;
    this.password = password;
  }

  public String getId() { return id; }

  public String getName() { return name; }

  public String getEmail() { return email; }

  public String getPassword() { return password; }

  public Collection<Task> getTasks() { return tasks; }

  public void setTasks(Collection<Task> tasks) { this.tasks = tasks; }
}
