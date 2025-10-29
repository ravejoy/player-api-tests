package com.ravejoy.player.testsupport;

public enum Editor {
  SUPERVISOR("supervisor"),
  ADMIN("admin"),
  USER("user");

  private final String value;

  Editor(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
