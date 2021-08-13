package com.pedrofrohmut.todos.web.controllers;

import com.pedrofrohmut.todos.domain.dtos.CreateUserDto;
import com.pedrofrohmut.todos.domain.dtos.SignInUserDto;
import com.pedrofrohmut.todos.domain.errors.InvalidUserException;
import com.pedrofrohmut.todos.domain.errors.PasswordAndHashDoNotMatchException;
import com.pedrofrohmut.todos.domain.errors.UserEmailAlreadyTakenException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByEmailException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.usecases.UserUseCase;
import com.pedrofrohmut.todos.infra.dataaccess.UserDataAccessImpl;
import com.pedrofrohmut.todos.infra.errors.InvalidTokenException;
import com.pedrofrohmut.todos.infra.errors.TokenExpiredException;
import com.pedrofrohmut.todos.infra.factories.ConnectionFactory;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.infra.services.JjwtJwtService;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.dtos.ControllerResponseDto;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

public class UserController {

  private UserUseCase userUseCase;

  public UserController() {
    this.userUseCase = createUserUseCase();
  }

  public UserController(UserUseCase  userUseCase) {
    this.userUseCase = userUseCase;
  }

  public ControllerResponseDto<?> create(AdaptedRequest<CreateUserDto> request) {
    try {
      userUseCase.create(request.body);
      return new ControllerResponseDto<>(201);
    } catch (MissingRequestBodyException | UserEmailAlreadyTakenException | InvalidUserException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> signIn(AdaptedRequest<SignInUserDto> request) {
    try {
      final var signedUser = userUseCase.signIn(request.body);
      return new ControllerResponseDto<>(200, signedUser);
    } catch (UserNotFoundByEmailException | PasswordAndHashDoNotMatchException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> getSignedUser(AdaptedRequest<?> request) {
    try {
      final var signedUser = userUseCase.getSigned(request.authUserId);
      return new ControllerResponseDto<>(200, signedUser);
    } catch (UserNotFoundByIdException | TokenExpiredException | InvalidTokenException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  private UserUseCase createUserUseCase() {
    final var connectionFactory = new ConnectionFactory();
    final var connection = connectionFactory.getConnection();
    final var userDataAccess = new UserDataAccessImpl(connection);
    final var passwordService = new BcryptPasswordService();
    final var jwtService = new JjwtJwtService();
    final var userService = new UserUseCase(userDataAccess, passwordService, jwtService);
    return userService;
  }

}
