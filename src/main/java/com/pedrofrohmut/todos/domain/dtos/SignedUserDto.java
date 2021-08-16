package com.pedrofrohmut.todos.domain.dtos;

public class SignedUserDto {

  public String id;
  public String name;
  public String email;
  public String token;

  public SignedUserDto() {
  }

  public SignedUserDto(String id, String name, String email, String token) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.token = token;
  }

}
