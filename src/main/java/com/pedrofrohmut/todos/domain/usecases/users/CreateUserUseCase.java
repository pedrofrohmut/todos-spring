package com.pedrofrohmut.todos.domain.usecases.users;

import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.CreateUserDto;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.errors.UserEmailAlreadyTakenException;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

public class CreateUserUseCase {

  private static final String errorMessage = "CreateUserUseCase execute";

  private final UserDataAccess userDataAccess;
  private final PasswordService passwordService;

  public CreateUserUseCase(UserDataAccess userDataAccess, PasswordService passwordService) {
    this.userDataAccess = userDataAccess;
    this.passwordService = passwordService;
  }

  public void execute(CreateUserDto newUser) {
    checkNewUser(newUser);
    checkIfEmailIsAlreadyTaken(newUser.email);
    final var passwordHash = getPasswordHash(newUser.password);
    createUser(newUser, passwordHash);
  }

  private void checkNewUser(CreateUserDto newUser) {
    if (newUser == null) {
      throw new MissingRequestBodyException(errorMessage);
    }
    User.validateName(newUser.name);
    User.validateEmail(newUser.email);
    User.validatePassword(newUser.password);
  }

  private void checkIfEmailIsAlreadyTaken(String email) {
    final var foundUser = userDataAccess.findByEmail(email);
    if (foundUser != null) {
      throw new UserEmailAlreadyTakenException(errorMessage);
    }
  }

  private String getPasswordHash(String password) {
    User.validatePassword(password);
    return passwordService.hashPassword(password);
  }

  private void createUser(CreateUserDto dto, String passwordHash) {
    final var newUser = new User(dto.name, dto.email);
    newUser.setPasswordHash(passwordHash);
    userDataAccess.create(newUser);
  }

}
