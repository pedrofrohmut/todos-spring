package com.pedrofrohmut.todos.web.adapter;

public class AdaptedRequest {

  public Object body;
  public String authUserId;
  public String param;

  public AdaptedRequest(Object body, String authUserId, String param) {
    this.body = body;
    this.authUserId = authUserId;
    this.param = param;
  }

}
