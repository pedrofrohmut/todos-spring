package com.pedrofrohmut.todos.web.controllers;

import com.pedrofrohmut.todos.domain.dtos.CreateUserDto;
import com.pedrofrohmut.todos.domain.dtos.SignInUserDto;
import com.pedrofrohmut.todos.domain.dtos.SignedUserDto;
import com.pedrofrohmut.todos.domain.errors.InvalidUserException;
import com.pedrofrohmut.todos.domain.errors.PasswordAndHashDoNotMatchException;
import com.pedrofrohmut.todos.domain.errors.UserEmailAlreadyTakenException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByEmailException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.domain.factories.UseCaseFactory;
import com.pedrofrohmut.todos.domain.usecases.users.CreateUserUseCase;
import com.pedrofrohmut.todos.domain.usecases.users.GetSignedUserUseCase;
import com.pedrofrohmut.todos.domain.usecases.users.SignInUserUseCase;
import com.pedrofrohmut.todos.infra.errors.InvalidTokenException;
import com.pedrofrohmut.todos.infra.errors.TokenExpiredException;
import com.pedrofrohmut.todos.infra.factories.ConnectionFactory;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.dtos.ControllerResponseDto;
import com.pedrofrohmut.todos.web.errors.MissingRequestAuthUserIdException;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

public class UserController {

  public ControllerResponseDto<?> create(AdaptedRequest<CreateUserDto> request) {
    return create(null, request);
  }

  public ControllerResponseDto<?> create(CreateUserUseCase createUserUseCase, AdaptedRequest<CreateUserDto> request) {
    try {
      if (createUserUseCase == null) {
        final var connection = ConnectionFactory.getConnection();
        final var useCase = (CreateUserUseCase) UseCaseFactory.getInstance("CreateUserUseCase", connection);
        useCase.execute(request.body);
      } else {
        createUserUseCase.execute(request.body);
      }
      return new ControllerResponseDto<>(201);
    } catch (MissingRequestBodyException | UserEmailAlreadyTakenException | InvalidUserException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> signIn(AdaptedRequest<SignInUserDto> request) {
    return signIn(null, request);
  }

  public ControllerResponseDto<?> signIn(SignInUserUseCase signInUserUseCase, AdaptedRequest<SignInUserDto> request) {
    try {
      SignedUserDto signedUser = null;
      if (signInUserUseCase == null) {
        final var connection = ConnectionFactory.getConnection();
        final var useCase = (SignInUserUseCase) UseCaseFactory.getInstance("SignInUserUseCase", connection);
        signedUser = useCase.execute(request.body);
      } else {
        signedUser = signInUserUseCase.execute(request.body);
      }
      return new ControllerResponseDto<>(200, signedUser);
    } catch (
        MissingRequestBodyException |
        InvalidUserException |
        UserNotFoundByEmailException |
        PasswordAndHashDoNotMatchException e
    ) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

  public ControllerResponseDto<?> getSigned(AdaptedRequest<?> request) {
    return getSigned(null, request);
  }

  public ControllerResponseDto<?> getSigned(GetSignedUserUseCase getSignedUserUseCase, AdaptedRequest<?> request) {
    try {
      SignedUserDto signedUser = null;
      if (getSignedUserUseCase == null) {
        final var connection = ConnectionFactory.getConnection();
        final var useCase = (GetSignedUserUseCase) UseCaseFactory.getInstance("GetSignedUserUseCase", connection);
        signedUser = useCase.execute(request.authUserId);
      } else {
        signedUser = getSignedUserUseCase.execute(request.authUserId);
      }
      return new ControllerResponseDto<>(200, signedUser);
    } catch (MissingRequestAuthUserIdException | UserNotFoundByIdException | TokenExpiredException | InvalidTokenException e) {
      return new ControllerResponseDto<>(400, e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      return new ControllerResponseDto<>(500, e.getMessage());
    }
  }

}
