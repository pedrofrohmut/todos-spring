package com.pedrofrohmut.todos.domain.entities;

import java.util.List;

import com.pedrofrohmut.todos.domain.errors.InvalidUserException;
import com.pedrofrohmut.utils.validation.Validator;

public class User {

  private final String id;
  private final String name;
  private final String email;

  private String password;
  private String passwordHash;

  private List<Task> tasks;

  public User(String id, String name, String email, String passwordHash) {
    Entity.validateId(id);
    User.validateName(name);
    User.validateEmail(email);
    User.validatePasswordHash(passwordHash);
    this.id = id;
    this.name = name;
    this.email = email;
    this.passwordHash = passwordHash;
  }

  public User(String id, String name, String email) {
    Entity.validateId(id);
    User.validateName(name);
    User.validateEmail(email);
    this.id = id;
    this.name = name;
    this.email = email;
  }

  public User(String name, String email) {
    User.validateName(name);
    User.validateEmail(email);
    this.id = "";
    this.name = name;
    this.email = email;
  }

  public static void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new InvalidUserException("Name is required and cannot be blank");
    }
    if (name.length() < 5 || name.length() > 120) {
      throw new InvalidUserException("Name must be between 5 and 120 characters");
    }
  }

  public static void validateEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new InvalidUserException("Email is required and cannot be blank");
    }
    if (!Validator.isEmail(email)) {
      throw new InvalidUserException("Email is not in a valid format");
    }
  }

  public static void validatePassword(String password) {
    if (password == null || password.isBlank()) {
      throw new InvalidUserException("Password is required and cannot be blank");
    }
    if (password.length() < 3 || password.length() > 32) {
      throw new InvalidUserException("Password must be between 5 and 32 characters");
    }
  }

  public static void validatePasswordHash(String passwordHash) {
    if (passwordHash == null || passwordHash.isBlank()) {
      throw new InvalidUserException("PasswordHash is in blank");
    }
  }

  public static void validateTasks(List<Task> tasks, String userId) {
    tasks.forEach(task -> {
      if (!task.getUserId().equals(userId)) {
        throw new InvalidUserException("Task do not belong to this user");
      }
    });
  }

  public String getId() { return id; }

  public String getName() { return name; }

  public String getEmail() { return email; }

  public String getPassword() { return password; }

  public void setPassword(String password) {
    User.validatePassword(password);
    this.password = password;
  }

  public String getPasswordHash() { return passwordHash; }

  public void setPasswordHash(String passwordHash) {
    User.validatePasswordHash(passwordHash);
    this.passwordHash = passwordHash;
  }

  public List<Task> getTasks() { return tasks; }

  public void setTasks(List<Task> tasks) {
    User.validateTasks(tasks, this.id);
    this.tasks = tasks;
  }

  @Override
  public String toString() {
    return "[ id: " + id +
      ", name: " + name +
      ", email: " + email +
      ", password: " + password +
      ", passwordHash: " + passwordHash + " ]";
  }

}
