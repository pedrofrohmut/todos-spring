package com.pedrofrohmut.todos.domain.dtos;

public class CreateUserDto {
  public String name;
  public String email;
  public String password;
  public String passwordHash;

  public CreateUserDto() {}

  public CreateUserDto(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
  }
}
