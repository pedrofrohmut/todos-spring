package com.pedrofrohmut.todos.web.adapter;

import java.lang.reflect.Method;

import com.pedrofrohmut.todos.infra.services.JjwtJwtService;
import com.pedrofrohmut.todos.web.dtos.ControllerResponseDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SpringAdapter {

  private static final String controllerPrefix = "com.pedrofrohmut.todos.web.controllers.";

  public static ResponseEntity<?> callController(
      String controllerClass,
      String controllerMethod,
      Object body,
      String token,
      String param
  ) {
    String authUserId = null;
    try {
      authUserId = new JjwtJwtService().getUserIdFromToken(token);
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(401));
    }
    try {
      final var controller = getController(controllerClass);
      final var method = getControllerMethod(controller, controllerClass, controllerMethod);
      final var adaptedRequest = new AdaptedRequest(body, authUserId, param);
      final var controllerResponse = invokeControllerMethod(method, controller, adaptedRequest);
      final var adaptedResponse = adaptResponse(controllerResponse);
      return adaptedResponse;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Deprecated
  public static ResponseEntity<?> callController(
      String controllerClassString,
      String controllerMethodString,
      AdaptedRequest adaptedRequest
  ) {
    try {
      final var controller = getController(controllerClassString);
      final var method = getControllerMethod(controller, controllerClassString, controllerMethodString);
      final var controllerResponse = invokeControllerMethod(method, controller, adaptedRequest);
      final var adaptedResponse = adaptResponse(controllerResponse);
      return adaptedResponse;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Object getController(String controllerClassString) throws Exception {
    final var controllerClass = Class.forName(controllerPrefix + controllerClassString);
    final var controller = controllerClass.getDeclaredConstructor().newInstance();
    return controller;
  }


  private static Method getControllerMethod(
      Object controller, String controllerClassString, String controllerMethodString)
      throws Exception {
    final var method = controller.getClass().getMethod(controllerMethodString, AdaptedRequest.class);
    return method;
  }

  private static ControllerResponseDto<?> invokeControllerMethod(
      Method method, Object controller, AdaptedRequest adaptedRequest) throws Exception {
    return (ControllerResponseDto<?>) method.invoke(controller, adaptedRequest);
  }

  private static ResponseEntity<?> adaptResponse(ControllerResponseDto<?> dto) {
    return ResponseEntity.status(dto.httpStatus).body(dto.body);
  }

}
