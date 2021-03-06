package com.pedrofrohmut.todos.domain.factories;

import java.sql.Connection;

import com.pedrofrohmut.todos.domain.errors.UseCaseNotListedException;
import com.pedrofrohmut.todos.domain.usecases.tasks.CreateTaskUseCase;
import com.pedrofrohmut.todos.domain.usecases.tasks.FindTaskByIdUseCase;
import com.pedrofrohmut.todos.domain.usecases.users.CreateUserUseCase;
import com.pedrofrohmut.todos.domain.usecases.users.GetSignedUserUseCase;
import com.pedrofrohmut.todos.domain.usecases.users.SignInUserUseCase;
import com.pedrofrohmut.todos.infra.dataaccess.TaskDataAccessImpl;
import com.pedrofrohmut.todos.infra.dataaccess.UserDataAccessImpl;
import com.pedrofrohmut.todos.infra.services.BcryptPasswordService;
import com.pedrofrohmut.todos.infra.services.JjwtJwtService;

public class UseCaseFactory {

  public static Object getInstance(String useCaseName, Connection connection) {
    switch (useCaseName) {
      case "CreateUserUseCase": {
        final var userDataAccess = new UserDataAccessImpl(connection);
        final var passwordService = new BcryptPasswordService();
        final var createUserUseCase = new CreateUserUseCase(userDataAccess, passwordService);
        return createUserUseCase;
      }
      case "SignInUserUseCase": {
        final var userDataAccess = new UserDataAccessImpl(connection);
        final var passwordService = new BcryptPasswordService();
        final var jwtService = new JjwtJwtService();
        final var signInUserUseCase = new SignInUserUseCase(userDataAccess, passwordService, jwtService);
        return signInUserUseCase;
      }
      case "GetSignedUserUseCase": {
        final var userDataAccess = new UserDataAccessImpl(connection);
        final var jwtService = new JjwtJwtService();
        final var getSignedUserUseCase = new GetSignedUserUseCase(userDataAccess, jwtService);
        return getSignedUserUseCase;
      }
      case "CreateTaskUseCase": {
        final var taskDataAccess = new TaskDataAccessImpl(connection);
        final var userDataAccess = new UserDataAccessImpl(connection);
        final var createTaskUseCase = new CreateTaskUseCase(taskDataAccess, userDataAccess);
        return createTaskUseCase;
      }
      case "FindTaskByIdUseCase": {
        final var taskDataAccess = new TaskDataAccessImpl(connection);
        final var userDataAccess = new UserDataAccessImpl(connection);
        final var findTaskByIdUseCase = new FindTaskByIdUseCase(taskDataAccess, userDataAccess);
        return findTaskByIdUseCase;
      }
      default:
        throw new UseCaseNotListedException();
    }
  }

}
