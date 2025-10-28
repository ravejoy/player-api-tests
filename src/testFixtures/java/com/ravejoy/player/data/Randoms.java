package com.ravejoy.player.data;

import java.security.SecureRandom;

public final class Randoms {
  private static final String ALNUM =
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final SecureRandom RND = new SecureRandom();

  private Randoms() {}

  public static String alnum(int len) {
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) sb.append(ALNUM.charAt(RND.nextInt(ALNUM.length())));
    return sb.toString();
  }

  public static String username(String prefix) {
    return prefix + alnum(8).toLowerCase();
  }

  public static String screenName(String prefix) {
    return prefix + "_" + alnum(6);
  }

  public static String validPassword() {
    int len = 8 + RND.nextInt(7);
    return alnum(len);
  }

  public static String invalidPasswordTooShort() {
    return alnum(3);
  }

  public static String invalidPasswordTooLong() {
    return alnum(20);
  }

  public static String invalidPasswordNonAlnum() {
    return "bad***";
  }

  public static int age17to59() {
    return 17 + RND.nextInt(43);
  }
}
