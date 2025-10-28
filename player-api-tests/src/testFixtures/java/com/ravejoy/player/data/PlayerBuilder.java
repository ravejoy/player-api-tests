package com.ravejoy.player.data;

import com.ravejoy.player.data.model.PlayerCreateData;
import com.ravejoy.player.players.dto.PlayerUpdateRequestDto;

public final class PlayerBuilder {

  private String login = Randoms.username("u");
  private String screenName = Randoms.screenName("s");
  private String role = "user";
  private int age = Randoms.age17to59();
  private String gender = "male";
  private String password = Randoms.validPassword();

  public static PlayerBuilder create() {
    return new PlayerBuilder();
  }

  public PlayerBuilder login(String v) {
    this.login = v;
    return this;
  }

  public PlayerBuilder screenName(String v) {
    this.screenName = v;
    return this;
  }

  public PlayerBuilder role(String v) {
    this.role = v;
    return this;
  }

  public PlayerBuilder age(int v) {
    this.age = v;
    return this;
  }

  public PlayerBuilder gender(String v) {
    this.gender = v;
    return this;
  }

  public PlayerBuilder password(String v) {
    this.password = v;
    return this;
  }

  public PlayerCreateData buildCreate() {
    return new PlayerCreateData(login, screenName, role, age, gender, password);
  }

  public PlayerUpdateRequestDto buildUpdate() {
    return new PlayerUpdateRequestDto(age, gender, login, password, role, screenName);
  }
}
