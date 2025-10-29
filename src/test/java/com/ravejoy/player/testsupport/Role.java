package com.ravejoy.player.testsupport;

public enum Role {
  USER("user"),
  ADMIN("admin"),
  SUPERVISOR("supervisor");

  private final String value;

  Role(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
