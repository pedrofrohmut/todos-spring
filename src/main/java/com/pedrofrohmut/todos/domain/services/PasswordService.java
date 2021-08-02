package com.pedrofrohmut.todos.domain.services;

public interface PasswordService {
  String hashPassword(String password);
  boolean comparePasswordAndHash(String password, String hash);
}
