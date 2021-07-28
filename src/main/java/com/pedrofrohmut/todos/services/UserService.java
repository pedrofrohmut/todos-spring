package com.pedrofrohmut.todos.services;

import com.pedrofrohmut.todos.dtos.CreateUserDto;
import com.pedrofrohmut.todos.dtos.SignInUserDto;
import com.pedrofrohmut.todos.dtos.SignedUserDto;
import com.pedrofrohmut.todos.dtos.UserDto;
import com.pedrofrohmut.todos.errors.PasswordAndHashDoNotMatchException;
import com.pedrofrohmut.todos.errors.UserEmailAlreadyTakenException;
import com.pedrofrohmut.todos.errors.UserNotFoundByEmailException;
import com.pedrofrohmut.todos.errors.UserNotFoundByIdException;
import com.pedrofrohmut.todos.repositories.UserRepository;

public class UserService {

  private static final String errorMessage = "[UserService] %s";

  private final UserRepository userRepository;
  private final PasswordService passwordService;
  private final JwtService jwtService;

  public UserService(UserRepository userRepository, PasswordService passwordService, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordService = passwordService;
    this.jwtService = jwtService;
  }

  public void create(CreateUserDto dto) {
    final var foundUser = this.userRepository.findByEmail(dto.email);
    if (foundUser != null) {
      throw new UserEmailAlreadyTakenException(String.format(UserService.errorMessage, "create"));
    }
    dto.passwordHash  = this.passwordService.hashPassword(dto.password);
    this.userRepository.create(dto);
  }

  public SignedUserDto signIn(SignInUserDto dto) {
    final var foundUser = this.userRepository.findByEmail(dto.email);
    if (foundUser == null) {
      throw new UserNotFoundByEmailException(String.format(UserService.errorMessage, "signIn"));
    }
    final var isMatch = this.passwordService.comparePasswordAndHash(dto.password, foundUser.passwordHash);
    if (!isMatch) {
      throw new PasswordAndHashDoNotMatchException(String.format(UserService.errorMessage, "signIn"));
    }
    final var token = this.jwtService.generateToken(foundUser.id);
    final var signedUser = mapFoundUserAndTokenToResultDto(foundUser, token);
    return signedUser;
  }

  private SignedUserDto mapFoundUserAndTokenToResultDto(UserDto foundUser, String token) {
    final var signedUser = new SignedUserDto();
    signedUser.id = foundUser.id;
    signedUser.name = foundUser.name;
    signedUser.email = foundUser.email;
    signedUser.token = token;
    return signedUser;
  }

  public SignedUserDto getSigned(String userId) {
    final var foundUser = this.userRepository.findById(userId);
    if (foundUser == null) {
      throw new UserNotFoundByIdException(String.format(UserService.errorMessage, "getSigned"));
    }
    final var token = this.jwtService.generateToken(userId);
    final var signedUser = mapFoundUserAndTokenToResultDto(foundUser, token);
    return signedUser;
  }

}
