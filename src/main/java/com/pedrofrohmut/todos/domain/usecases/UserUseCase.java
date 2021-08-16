package com.pedrofrohmut.todos.domain.usecases;

import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.CreateUserDto;
import com.pedrofrohmut.todos.domain.dtos.SignedUserDto;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.errors.UserEmailAlreadyTakenException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.mapper.UserMapper;
import com.pedrofrohmut.todos.domain.services.JwtService;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

public class UserUseCase {

  private static final String errorMessage = "[UserUseCase] %s";

  private final UserDataAccess userDataAccess;
  private final PasswordService passwordService;
  private final JwtService jwtService;

  public UserUseCase(
      UserDataAccess userDataAccess, PasswordService passwordService, JwtService jwtService) {
    this.userDataAccess = userDataAccess;
    this.passwordService = passwordService;
    this.jwtService = jwtService;
  }

  public void create(CreateUserDto dto) {
    if (dto == null) {
      throw new MissingRequestBodyException(String.format(UserUseCase.errorMessage, "create"));
    }
    final var foundUser = this.userDataAccess.findByEmail(dto.email);
    if (foundUser != null) {
      throw new UserEmailAlreadyTakenException(String.format(UserUseCase.errorMessage, "create"));
    }
    User.validatePassword(dto.password);
    final var passwordHash  = this.passwordService.hashPassword(dto.password);
    final var newUser = new User(dto.name, dto.email);
    newUser.setPasswordHash(passwordHash);
    this.userDataAccess.create(newUser);
  }

  public SignedUserDto getSigned(String userId) {
    final var foundUser = this.userDataAccess.findById(userId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(UserUseCase.errorMessage, "getSigned"));
    }
    final var token = this.jwtService.generateToken(userId);
    final var signedUser = UserMapper.mapEntityAndTokenToSignedUserDto(foundUser, token);
    return signedUser;
  }

}
