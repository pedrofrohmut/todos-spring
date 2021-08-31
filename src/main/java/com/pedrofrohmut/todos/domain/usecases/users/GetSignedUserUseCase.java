package com.pedrofrohmut.todos.domain.usecases.users;

import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.dtos.SignedUserDto;
import com.pedrofrohmut.todos.domain.entities.Entity;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.mapper.UserMapper;
import com.pedrofrohmut.todos.domain.services.JwtService;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;

public class GetSignedUserUseCase {

  private static final String errorMessage = "GetSignedUserUseCase execute";

  private final UserDataAccess userDataAccess;
  private final JwtService jwtService;

  public GetSignedUserUseCase(UserDataAccess userDataAccess, JwtService jwtService) {
    this.userDataAccess = userDataAccess;
    this.jwtService = jwtService;
  }

  public SignedUserDto execute(String authUserId) {
    checkIfAuthUserIdIsNull(authUserId);
    final var foundUser = findUserById(authUserId);
    final var token = getToken(authUserId);
    final var signedUser = getSignedUserDto(foundUser, token);
    return signedUser;
  }

  private void checkIfAuthUserIdIsNull(String authUserId) {
    if (authUserId == null || authUserId == "") {
      throw new MissingRequestAuthUserIdException(errorMessage);
    }
  }

  private User findUserById(String authUserId) {
    Entity.validateId(authUserId);
    final var foundUser = userDataAccess.findById(authUserId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(errorMessage);
    }
    return foundUser;
  }

  private String getToken(String authUserId) {
    return jwtService.generateToken(authUserId);
  }

  private SignedUserDto getSignedUserDto(User foundUser, String token) {
    return UserMapper.mapEntityAndTokenToSignedUserDto(foundUser, token);
  }

}
