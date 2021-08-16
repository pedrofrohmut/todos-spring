package com.pedrofrohmut.todos.web.controllers.users;

import com.pedrofrohmut.todos.domain.dtos.SignInUserDto;
import com.pedrofrohmut.todos.domain.errors.InvalidUserException;
import com.pedrofrohmut.todos.domain.errors.PasswordAndHashDoNotMatchException;
import com.pedrofrohmut.todos.domain.errors.UserNotFoundByEmailException;
import com.pedrofrohmut.todos.domain.usecases.users.SignInUserUseCase;
import com.pedrofrohmut.todos.infra.dataaccess.UserDataAccessImpl;
import com.pedrofrohmut.todos.infra.factories.ConnectionFactory;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.infra.services.JjwtJwtService;
import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.dtos.ControllerResponseDto;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

public class SignInUserController {

  private SignInUserUseCase signInUserUseCase;

  public SignInUserController() {
    final var connectionFactory = new ConnectionFactory();
    final var connection = connectionFactory.getConnection();
    final var userDataAccess = new UserDataAccessImpl(connection);
    final var passwordService = new BcryptPasswordService();
    final var jwtService = new JjwtJwtService();
    this.signInUserUseCase = new SignInUserUseCase(userDataAccess, passwordService, jwtService);
  }

  public SignInUserController(SignInUserUseCase signInUserUseCase) {
    this.signInUserUseCase = signInUserUseCase;
  }

  public ControllerResponseDto<?> execute(AdaptedRequest<SignInUserDto> request) {
    try {
      final var signedUser = signInUserUseCase.execute(request.body);
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

}
