package com.pedrofrohmut.todos.domain.mapper;

import com.pedrofrohmut.todos.domain.dtos.SignedUserDto;
import com.pedrofrohmut.todos.domain.entities.User;

public class UserMapper {

  public static SignedUserDto mapEntityAndTokenToSignedUserDto(User user, String token) {
    final var signedUserDto = new SignedUserDto();
    signedUserDto.id = user.getId();
    signedUserDto.name = user.getName();
    signedUserDto.email = user.getEmail();
    signedUserDto.token = token;
    return signedUserDto;
  }

}
