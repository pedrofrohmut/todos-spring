package com.pedrofrohmut.todos.web.routes;

import com.pedrofrohmut.todos.domain.dtos.CreateUserDto;
import com.pedrofrohmut.todos.domain.dtos.SignInUserDto;
import com.pedrofrohmut.todos.web.adapter.SpringAdapter;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping("/api/users")
public class UserRoutes {

  @PostMapping
  public ResponseEntity<?> create(@RequestBody CreateUserDto dto) {
    return SpringAdapter.callController("UserController", "create", dto, null, null);
  }

  @PostMapping("/signin")
  public ResponseEntity<?> signIn(@RequestBody SignInUserDto dto) {
    return SpringAdapter.callController("UserController", "signIn", dto, null, null);
  }

  @GetMapping("/signed")
  public ResponseEntity<?> getSignedUser(@RequestHeader("authentication_token") String token) {
    return SpringAdapter.callController("UserController", "getSignedUser", null, token, null);
  }

}
