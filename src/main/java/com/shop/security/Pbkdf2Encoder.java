package com.shop.security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Pbkdf2Encoder implements PasswordEncoder {

  private static final int ITERATIONS = 65536;
  private static final int KEY_LENGTH = 256;
  private static final SecureRandom RANDOM = new SecureRandom();

  @Override
  public String encode(CharSequence rawPassword) {
    byte[] salt = new byte[16];
    RANDOM.nextBytes(salt);
    byte[] hash = pbkdf2(rawPassword.toString().toCharArray(), salt, ITERATIONS);
    return ITERATIONS
        + ":"
        + Base64.getEncoder().encodeToString(salt)
        + ":"
        + Base64.getEncoder().encodeToString(hash);
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    String[] parts = encodedPassword.split(":");
    int iterations = Integer.parseInt(parts[0]);
    byte[] salt = Base64.getDecoder().decode(parts[1]);
    byte[] expectedHash = Base64.getDecoder().decode(parts[2]);
    byte[] actualHash = pbkdf2(rawPassword.toString().toCharArray(), salt, iterations);
    return MessageDigest.isEqual(expectedHash, actualHash);
  }

  private byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
    try {
      PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH);
      return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
    } catch (Exception e) {
      throw new IllegalStateException("PBKDF2 error", e);
    }
  }
}
