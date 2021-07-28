package com.pedrofrohmut.todos.dtos;

import org.springframework.stereotype.Component;

@Component
public class CreateUserDto {
  public String name;
  public String email;
  public String password;
  public String passwordHash;
}
