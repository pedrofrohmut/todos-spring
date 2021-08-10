package com.pedrofrohmut.todos.web.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import com.pedrofrohmut.todos.web.adapter.AdaptedRequest;
import com.pedrofrohmut.todos.web.errors.MissingRequestBodyException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserControllerTests {

  AdaptedRequest request;

  @BeforeEach
  void beforeEach() {
    request = new AdaptedRequest(null, null, null);
  }

  @Test
  @DisplayName("Null body -> 400/message")
  void test1() {
    // Given
    assertThat(request.body).isNull();
    assertThat(request.authUserId).isNull();
    assertThat(request.param).isNull();
    // Then
    final var controllerResponse = new UserController().create(request);
    // When
    assertThat(controllerResponse).isNotNull();
    assertThat(controllerResponse.httpStatus).isEqualTo(400);
    assertThat(controllerResponse.body.toString()).contains(MissingRequestBodyException.message);
  }

}
