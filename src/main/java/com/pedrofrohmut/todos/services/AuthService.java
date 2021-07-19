package com.pedrofrohmut.todos.services;

import com.pedrofrohmut.todos.errors.NotImplementedException;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class AuthService {

  public String hashPassword(String password) {
    final var passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
    return passwordHash;
  }

  public boolean comparePasswordAndHash(String password, String hash) {
    throw new NotImplementedException();
  }

  public String generateToken(String userId) {
    throw new NotImplementedException();
  }

}
