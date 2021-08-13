package com.pedrofrohmut.todos.web.adapter;

public class AdaptedRequest<T> {

  public T body;
  public String authUserId;
  public String param;

  public AdaptedRequest(T body, String authUserId, String param) {
    this.body = body;
    this.authUserId = authUserId;
    this.param = param;
  }

}
