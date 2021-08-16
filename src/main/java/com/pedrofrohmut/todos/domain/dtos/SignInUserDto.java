package com.pedrofrohmut.todos.domain.dtos;

public class SignInUserDto {
  public String email;
  public String password;

  public SignInUserDto(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
