package com.pedrofrohmut.todos.controllers;

import com.pedrofrohmut.todos.dtos.CreateUserDto;
import com.pedrofrohmut.todos.dtos.SignInUserDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/users")
public class UserController {

  // Create User Route
  @PostMapping
  public ResponseEntity<?> create(@RequestBody CreateUserDto dto) {
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }


  // Sign In User Route
  @PostMapping("/signin")
  public ResponseEntity<?> signIn(@RequestBody SignInUserDto dto) {
    return new ResponseEntity<>(dto, HttpStatus.OK);
  }

  // Get Signed User Route
  @GetMapping("/signed")
  public ResponseEntity<?> getSignedUser() {
    return new ResponseEntity<>("Get Signed User", HttpStatus.OK);
  }

}
