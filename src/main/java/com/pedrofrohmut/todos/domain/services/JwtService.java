package com.pedrofrohmut.todos.domain.services;

public interface JwtService {
  String generateToken(String userId);
  String getUserIdFromToken(String token);
}
