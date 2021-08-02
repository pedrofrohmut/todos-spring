package com.pedrofrohmut.utils.validation;

import java.util.regex.Pattern;

public class Validator {

  private static final String emailRegex =
    "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

  public static boolean isEmail(String email) {
    final var isEmail = Pattern.matches(emailRegex, email);
    return isEmail;
  }

}
