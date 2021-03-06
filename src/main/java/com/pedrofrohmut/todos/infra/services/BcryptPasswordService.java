package com.pedrofrohmut.todos.infra.services;

import com.pedrofrohmut.todos.domain.services.PasswordService;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class BcryptPasswordService implements PasswordService {

  public String hashPassword(String password) {
    final var hasher = BCrypt.withDefaults();
    final var COST = 12;
    final var passwordHash = hasher.hashToString(COST, password.toCharArray());
    return passwordHash;
  }

  public boolean comparePasswordAndHash(String password, String hashedPassword) {
    final var verifyer = BCrypt.verifyer();
    final var verifyResult = verifyer.verify(password.toCharArray(), hashedPassword);
    final var isMatch = verifyResult.verified;
    return isMatch;
  }

}
