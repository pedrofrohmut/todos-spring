package com.pedrofrohmut.todos.web.adapter;

import java.lang.reflect.Method;

import com.pedrofrohmut.todos.infra.errors.DecodeJWTException;
import com.pedrofrohmut.todos.infra.services.JjwtJwtService;
import com.pedrofrohmut.todos.web.dtos.ControllerResponseDto;
import com.pedrofrohmut.todos.web.errors.ControllerMethodNotFoundException;
import com.pedrofrohmut.todos.web.errors.ControllerNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SpringAdapter {

  private static final String CONTROLLER_PREFIX = "com.pedrofrohmut.todos.web.controllers.";
  private static final String errorMessage = "[SpringAdapter] callController";

  public static ResponseEntity<?> callController(
      String controllerClass,
      String controllerMethod,
      Object body,
      String token,
      String param
  ) {
    try {
      final var authUserId = getAuthUserId(token);
      final var controller = getController(controllerClass);
      final var method = getControllerMethod(controller, controllerMethod);
      final var adaptedRequest = new AdaptedRequest<>(body, authUserId, param);
      final var controllerResponse = invokeControllerMethod(method, controller, adaptedRequest);
      final var adaptedResponse = getAdaptResponse(controllerResponse);
      return adaptedResponse;
    } catch (DecodeJWTException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(401));
    } catch (ControllerNotFoundException | ControllerMethodNotFoundException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(404));
    } catch (Exception e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.valueOf(500));
    }
  }

  private static String getAuthUserId(String token) {
    if (token == null || token == "") {
      return "";
    }
    final var authUserId = new JjwtJwtService().getUserIdFromToken(token);
    return authUserId;
  }

  private static Object getController(String className) {
    try {
      final var controllerClass = Class.forName(CONTROLLER_PREFIX + className);
      final var controller = controllerClass.getDeclaredConstructor().newInstance();
      return controller;
    } catch (ClassNotFoundException e) {
      throw new ControllerNotFoundException(errorMessage);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Method getControllerMethod(Object controller, String methodName) {
    try {
      final var method = controller.getClass().getMethod(methodName, AdaptedRequest.class);
      return method;
    } catch (NoSuchMethodException e) {
      throw new ControllerMethodNotFoundException(errorMessage);
    }
  }

  private static ControllerResponseDto<?> invokeControllerMethod(
      Method method, Object controller, AdaptedRequest<?> adaptedRequest) throws Exception {
    return (ControllerResponseDto<?>) method.invoke(controller, adaptedRequest);
  }

  private static ResponseEntity<?> getAdaptResponse(ControllerResponseDto<?> dto) {
    return ResponseEntity.status(dto.httpStatus).body(dto.body);
  }

}
