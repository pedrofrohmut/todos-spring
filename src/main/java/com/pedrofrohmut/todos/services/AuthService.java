package com.pedrofrohmut.todos.services;

import java.util.Date;

import javax.crypto.SecretKey;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class AuthService {

  public String hashPassword(String password) {
    final var hasher = BCrypt.withDefaults();
    final var passwordAsCharArray = password.toCharArray();
    final var passwordHash = hasher.hashToString(12, passwordAsCharArray);
    return passwordHash;
  }

  public boolean comparePasswordAndHash(String password, String hash) {
    final var verifyer = BCrypt.verifyer();
    final var passwordAsCharArray = password.toCharArray();
    final var verifyResult = verifyer.verify(passwordAsCharArray, hash);
    final boolean isMatch = verifyResult.verified;
    return isMatch;
  }

  private SecretKey getSignKey() {
    var signKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    return signKey;
  }

  private String buildJwt(String userId, Date expires, SecretKey key) {
    return Jwts
      .builder()
      .signWith(key)
      .setExpiration(expires)
      .claim("userId", userId)
      .compact();
  }

  private Date getExpirationDate() {
    var oneDay = 1000 * 60 * 60 * 24;
    var now = new Date().getTime();
    var expirationDate = new Date(now + oneDay);
    return expirationDate;
  }

  public String generateToken(String userId) {
    var expiration = getExpirationDate();
    var signKey = getSignKey();
    var jws = buildJwt(userId, expiration, signKey);
    return jws;
  }

  // DECODE
  // assert Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jws).getBody().getSubject().equals("Joe");

}
