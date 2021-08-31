package com.pedrofrohmut.adapters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.pedrofrohmut.todos.domain.services.JwtService;
import com.pedrofrohmut.todos.infra.services.JjwtJwtService;
import com.pedrofrohmut.todos.web.adapter.SpringAdapter;
import com.pedrofrohmut.todos.web.errors.ControllerMethodNotFoundException;
import com.pedrofrohmut.todos.web.errors.ControllerNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration")
@DisplayName("SpringAdapterTests callController")
public class SpringAdapterTests {

  static final String USER_ID = UUID.randomUUID().toString();
  static final String CONTROLLER_NAME = "UserController";
  static final String CONTROLLER_METHOD = "getSigned";
  static final Object REQUEST_BODY = null;
  static final String REQUEST_PARAMS = null;

  final JwtService jwtService;
  final String TOKEN;

  public SpringAdapterTests() {
    jwtService = new JjwtJwtService();
    TOKEN = jwtService.generateToken(USER_ID);
  }

  @Test
  @DisplayName("Empty token throw no errors from SpringAdapter")
  void emptyToken() {
    final var emptyToken = "";
    // When
    final var responseEntity =
      SpringAdapter.callController(CONTROLLER_NAME, CONTROLLER_METHOD, REQUEST_BODY, emptyToken, REQUEST_PARAMS);
    // Then
    assertThat(responseEntity.getStatusCodeValue()).isNotEqualTo(401);
  }

  @Test
  @DisplayName("Invalid token => 401/message")
  void invalidToken() {
    final var invalidToken = "TOKEN";
    final var decodeTokenErr = getDecodeTokenErr(invalidToken);
    // Given
    assertThat(decodeTokenErr).isNotNull();
    // When
    final var responseEntity =
      SpringAdapter.callController(CONTROLLER_NAME, CONTROLLER_METHOD, REQUEST_BODY, invalidToken, REQUEST_PARAMS);
    // Then
    assertThat(responseEntity.getStatusCodeValue()).isEqualTo(401);
    assertThat(responseEntity.getBody().toString()).contains(decodeTokenErr.getMessage());
  }

  private Exception getDecodeTokenErr(String token) {
    try {
      jwtService.getUserIdFromToken(token);
      return null;
    } catch (Exception e) {
      return e;
    }
  }

  @Test
  @DisplayName("Controller not found => 404/message")
  void controllerNotFound() {
    final var invalidControllerName = "InvalidController";
    // When
    final var responseEntity =
      SpringAdapter.callController(invalidControllerName, CONTROLLER_METHOD, REQUEST_BODY, TOKEN, REQUEST_PARAMS);
    // Then
    assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    assertThat(responseEntity.getBody().toString()).contains(ControllerNotFoundException.message);
  }

  @Test
  @DisplayName("Controller method not found => 404/message")
  void controllerMethodNotFound() {
    final var invalidMethodName = "invalidMethod";
    // When
    final var responseEntity =
      SpringAdapter.callController(CONTROLLER_NAME, invalidMethodName, REQUEST_BODY, TOKEN, REQUEST_PARAMS);
    // Then
    assertThat(responseEntity.getStatusCodeValue()).isEqualTo(404);
    assertThat(responseEntity.getBody().toString()).contains(ControllerMethodNotFoundException.message);
  }

  @Test
  @DisplayName("Existing controller and controller method !=> 404")
  void existingControllerAndMethod() {
    // When
    final var responseEntity =
      SpringAdapter.callController(CONTROLLER_NAME, CONTROLLER_METHOD, REQUEST_BODY, TOKEN, REQUEST_PARAMS);
    // Then
    assertThat(responseEntity.getStatusCodeValue()).isNotEqualTo(404);
  }

}
