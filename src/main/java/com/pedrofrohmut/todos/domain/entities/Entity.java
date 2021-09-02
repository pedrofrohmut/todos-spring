package com.pedrofrohmut.todos.domain.entities;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.errors.InvalidEntityException;

public class Entity {

  public static void validateId(String id) {
    if (id == null || id.isBlank()) {
      throw new InvalidEntityException("Id is required and cannot be blank");
    }
    try {
      UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new InvalidEntityException("Id must be a valid UUIDv4");
    }
  }

}
