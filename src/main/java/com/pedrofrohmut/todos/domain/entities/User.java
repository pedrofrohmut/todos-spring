package com.pedrofrohmut.todos.domain.entities;

import java.util.List;

import com.pedrofrohmut.todos.domain.errors.InvalidUserException;
import com.pedrofrohmut.utils.validation.Validator;

public class User extends Entity {

  private final String id;
  private final String name;
  private final String email;

  private String password;
  private String passwordHash;

  private List<Task> tasks;

  public User(String id, String name, String email) {
    this.validateId(id);
    this.validateName(name);
    this.validateEmail(email);
    this.id = id;
    this.name = name;
    this.email = email;
  }

  public User(String name, String email) {
    this.validateName(name);
    this.validateEmail(email);
    this.id = "";
    this.name = name;
    this.email = email;
  }

  private void validateName(String name) {
    if (name.isBlank()) {
      throw new InvalidUserException("Name is required and cannot be blank");
    }
    if (name.length() < 5 || name.length() > 120) {
      throw new InvalidUserException("Name must be between 5 and 120 characters");
    }
  }

  private void validateEmail(String email) {
    if (email.isBlank()) {
      throw new InvalidUserException("Email is required and cannot be blank");
    }
    if (!Validator.isEmail(email)) {
      throw new InvalidUserException("Email is not in a valid format");
    }
  }

  private void validatePassword(String password) {
    if (password.isBlank()) {
      throw new InvalidUserException("Password is required and cannot be blank");
    }
    if (password.length() < 3 || password.length() > 32) {
      throw new InvalidUserException("Password must be between 5 and 32 characters");
    }
  }

  private void validatePasswordHash(String passwordHash) {
    if (passwordHash.isBlank()) {
      throw new InvalidUserException("PasswordHash is in blank");
    }
  }

  private void validateTasks(List<Task> tasks) {
    tasks.forEach(task -> {
      if (!task.getUserId().equals(id)) {
        throw new InvalidUserException("Task do not belong to this user");
      }
    });
  }

  public String getId() { return id; }

  public String getName() { return name; }

  public String getEmail() { return email; }

  public String getPassword() { return password; }

  public void setPassword(String password) {
    this.validatePassword(password);
    this.password = password;
  }

  public String getPasswordHash() { return passwordHash; }

  public void setPasswordHash(String passwordHash) {
    this.validatePasswordHash(passwordHash);
    this.passwordHash = passwordHash;
  }

  public List<Task> getTasks() { return tasks; }

  public void setTasks(List<Task> tasks) {
    this.validateTasks(tasks);
    this.tasks = tasks;
  }

}
