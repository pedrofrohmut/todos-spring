package com.pedrofrohmut.todos.domain.usecases.users;

import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.SignInUserDto;
import com.pedrofrohmut.todos.domain.dtos.SignedUserDto;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.errors.PasswordAndHashDoNotMatchException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByEmailException;
import com.pedrofrohmut.todos.domain.mapper.UserMapper;
import com.pedrofrohmut.todos.domain.services.JwtService;
import com.pedrofrohmut.todos.domain.services.PasswordService;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

public class SignInUserUseCase {

  private static final String errorMessage = "SignInUseCase execute";

  private final UserDataAccess userDataAccess;
  private final PasswordService passwordService;
  private final JwtService jwtService;

  public SignInUserUseCase(UserDataAccess userDataAccess, PasswordService passwordService, JwtService jwtService) {
    this.userDataAccess = userDataAccess;
    this.passwordService = passwordService;
    this.jwtService = jwtService;
  }

  public SignedUserDto execute(SignInUserDto credentials) {
    checkIfBodyIsNull(credentials);
    final var foundUser = findUserByEmail(credentials.email);
    checkPassword(credentials.password, foundUser);
    final var signedUser = generateTokenAndMapSignedUser(foundUser);
    return signedUser;
  }

  private void checkIfBodyIsNull(SignInUserDto dto) {
    if (dto == null) {
      throw new MissingRequestBodyException(errorMessage);
    }
  }

  private User findUserByEmail(String email) {
    User.validateEmail(email);
    final var foundUser = userDataAccess.findByEmail(email);
    if (foundUser == null) {
      throw new UserNotFoundByEmailException(errorMessage);
    }
    return foundUser;
  }

  private void checkPassword(String password, User foundUser) {
    User.validatePassword(password);
    final var isMatch = passwordService.comparePasswordAndHash(password, foundUser.getPasswordHash());
    if (!isMatch) {
      throw new PasswordAndHashDoNotMatchException(errorMessage);
    }
  }

  private SignedUserDto generateTokenAndMapSignedUser(User foundUser) {
    final var token = jwtService.generateToken(foundUser.getId());
    final var signedUser = UserMapper.mapEntityAndTokenToSignedUserDto(foundUser, token);
    return signedUser;
  }

}
