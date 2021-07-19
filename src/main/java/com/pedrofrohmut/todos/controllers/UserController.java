package com.pedrofrohmut.todos.controllers;

import com.pedrofrohmut.todos.dtos.CreateUserDto;
import com.pedrofrohmut.todos.dtos.SignInUserDto;
import com.pedrofrohmut.todos.errors.UserEmailAlreadyTakenException;
import com.pedrofrohmut.todos.repositories.UserRepository;
import com.pedrofrohmut.todos.services.AuthService;
import com.pedrofrohmut.todos.services.UserService;
import com.pedrofrohmut.todos.utils.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
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

  // Create User Route
  @PostMapping
  public ResponseEntity<?> create(@RequestBody CreateUserDto dto) {
    try {
      final var connectionFactory = new ConnectionFactory();
      final var connection = connectionFactory.getConnection();
      final var userRepository = new UserRepository(connection);
      final var authService = new AuthService();
      final var userService = new UserService(userRepository, authService);
      userService.create(dto);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UserEmailAlreadyTakenException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Sign In User Route
  @PostMapping("/signin")
  public ResponseEntity<?> signIn(@RequestBody SignInUserDto dto) {
    return new ResponseEntity<>(dto, HttpStatus.OK);
    // try {
      // final var signedUser = this.userService.signIn(dto);
      // return new ResponseEntity<>(signedUser, HttpStatus.OK);
    // } catch (UserNotFoundByEmailException | UserPasswordIsNotAMatchException e) {
    //   return new ResponseEntity<>(e.message, HttpStatus.BAD_REQUEST);
    // }
  }

  // Get Signed User Route
  @GetMapping("/signed")
  public ResponseEntity<?> getSignedUser(@RequestHeader("authentication_token") String token) {
    return new ResponseEntity<>(token, HttpStatus.OK);
    // try {
    //   final var signedUser = this.userService.getSigned(token);
    //   return new ResponseEntity<>(signedUser, HttpStatus.OK);
    // } catch (UserNotFoundByIdException | TokenExpiredException | InvalidTokenException e) {
    //   return new ResponseEntity<>(e.message, HttpStatus.BAD_REQUEST);
    // }
  }

}
