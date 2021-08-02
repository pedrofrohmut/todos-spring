package com.pedrofrohmut.todos.domain.dataaccess;

import com.pedrofrohmut.todos.domain.entities.User;

public interface UserDataAccess {
  User findByEmail(String email);
  User findById(String userId);
  void create(User newUser);
}
