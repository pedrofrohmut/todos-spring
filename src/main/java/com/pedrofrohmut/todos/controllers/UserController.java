package com.pedrofrohmut.todos.controllers;

import com.pedrofrohmut.todos.dtos.CreateUserDto;
import com.pedrofrohmut.todos.dtos.SignInUserDto;
import com.pedrofrohmut.todos.errors.InvalidTokenException;
import com.pedrofrohmut.todos.errors.PasswordAndHashDoNotMatchException;
import com.pedrofrohmut.todos.errors.TokenExpiredException;
import com.pedrofrohmut.todos.errors.UserEmailAlreadyTakenException;
import com.pedrofrohmut.todos.errors.UserNotFoundByEmailException;
import com.pedrofrohmut.todos.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.repositories.UserRepository;
import com.pedrofrohmut.todos.services.JwtService;
import com.pedrofrohmut.todos.services.PasswordService;
import com.pedrofrohmut.todos.services.UserService;
import com.pedrofrohmut.todos.utils.ConnectionFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
public class UserController {

  private UserService createUserService() {
    final var connectionFactory = new ConnectionFactory();
    final var connection = connectionFactory.getConnection();
    final var userRepository = new UserRepository(connection);
    final var passwordService = new PasswordService();
    final var jwtService = new JwtService();
    final var userService = new UserService(userRepository, passwordService, jwtService);
    return userService;
  }

  @PostMapping
  public ResponseEntity<?> create(@RequestBody CreateUserDto dto) {
    try {
      final var userService = createUserService();
      userService.create(dto);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UserEmailAlreadyTakenException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/signin")
  public ResponseEntity<?> signIn(@RequestBody SignInUserDto dto) {
    try {
      final var userService = createUserService();
      final var signedUser = userService.signIn(dto);
      return new ResponseEntity<>(signedUser, HttpStatus.OK);
    } catch (UserNotFoundByEmailException | PasswordAndHashDoNotMatchException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/signed")
  public ResponseEntity<?> getSignedUser(@RequestHeader("authentication_token") String token) {
    try {
      final var jwtService = new JwtService();
      final var userService = createUserServicePassingJwtService(jwtService);
      final var userId = jwtService.getUserIdFromToken(token);
      final var signedUser = userService.getSigned(userId);
      return new ResponseEntity<>(signedUser, HttpStatus.OK);
    } catch (UserNotFoundByIdException | TokenExpiredException | InvalidTokenException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  private UserService createUserServicePassingJwtService(JwtService jwtService) {
    final var connectionFactory = new ConnectionFactory();
    final var connection = connectionFactory.getConnection();
    final var userRepository = new UserRepository(connection);
    final var passwordService = new PasswordService();
    final var userService = new UserService(userRepository, passwordService, jwtService);
    return userService;
  }

}
