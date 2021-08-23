package com.pedrofrohmut.todos.mocks;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.dataaccess.UserDataAccess;
import com.pedrofrohmut.todos.domain.entities.User;
import com.pedrofrohmut.todos.domain.services.PasswordService;

public class UserDataAccessMock {

  public static UserDataAccess getMockForUserFoundByEmail(
      String name, String email, String password, PasswordService passwordService) {
    final var passwordHash = passwordService.hashPassword(password);
    final var userDB = new User(UUID.randomUUID().toString(), name, email, passwordHash);
    final var mockUserDataAccess = mock(UserDataAccess.class);
    when(mockUserDataAccess.findByEmail(email)).thenReturn(userDB);
    return mockUserDataAccess;
  }

  public static UserDataAccess getMockForUserNotFoundByEmail(String email) {
    final var mockUserDataAccess = mock(UserDataAccess.class);
    when(mockUserDataAccess.findByEmail(email)).thenReturn(null);
    return mockUserDataAccess;
  }

  public static UserDataAccess getMockForUserFoundById(
      String userId, String name, String email, String password, PasswordService passwordService) {
    final var passwordHash = passwordService.hashPassword(password);
    final var userDB = new User(userId, name, email, passwordHash);
    final var mockUserDataAccess = mock(UserDataAccess.class);
    when(mockUserDataAccess.findById(userId)).thenReturn(userDB);
    return mockUserDataAccess;
  }


  public static UserDataAccess getMockForUserNotFoundById(String userId) {
    final var mockUserDataAccess = mock(UserDataAccess.class);
    when(mockUserDataAccess.findById(userId)).thenReturn(null);
    return mockUserDataAccess;
  }

}
