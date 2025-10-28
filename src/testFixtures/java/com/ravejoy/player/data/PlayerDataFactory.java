package com.ravejoy.player.data;

import com.ravejoy.player.data.model.PlayerCreateData;
import com.ravejoy.player.players.dto.PlayerUpdateRequestDto;

public final class PlayerDataFactory {
  private PlayerDataFactory() {}

  public static PlayerCreateData validUser() {
    return PlayerBuilder.create().role("user").buildCreate();
  }

  public static PlayerCreateData validAdminSelf(String login) {
    return PlayerBuilder.create().login(login).role("admin").buildCreate();
  }

  public static PlayerCreateData invalidAgeTooYoung() {
    return PlayerBuilder.create().age(16).buildCreate();
  }

  public static PlayerCreateData invalidAgeTooOld() {
    return PlayerBuilder.create().age(60).buildCreate();
  }

  public static PlayerCreateData invalidGender() {
    return PlayerBuilder.create().gender("other").buildCreate();
  }

  public static PlayerCreateData invalidRole() {
    return PlayerBuilder.create().role("supervisor").buildCreate();
  }

  public static PlayerCreateData invalidPasswordTooShort() {
    return PlayerBuilder.create().password(Randoms.invalidPasswordTooShort()).buildCreate();
  }

  public static PlayerCreateData invalidPasswordTooLong() {
    return PlayerBuilder.create().password(Randoms.invalidPasswordTooLong()).buildCreate();
  }

  public static PlayerCreateData invalidPasswordNonAlnum() {
    return PlayerBuilder.create().password(Randoms.invalidPasswordNonAlnum()).buildCreate();
  }

  public static PlayerUpdateRequestDto updateScreenNameOnly(String newScreenName) {
    return new PlayerUpdateRequestDto(null, null, null, null, null, newScreenName);
  }

  public static PlayerUpdateRequestDto updatePasswordValid() {
    return new PlayerUpdateRequestDto(null, null, null, Randoms.validPassword(), null, null);
  }

  public static PlayerUpdateRequestDto updatePasswordInvalidNonAlnum() {
    return new PlayerUpdateRequestDto(
        null, null, null, Randoms.invalidPasswordNonAlnum(), null, null);
  }

  public static PlayerUpdateRequestDto updateAge(int age) {
    return new PlayerUpdateRequestDto(age, null, null, null, null, null);
  }

  public static PlayerUpdateRequestDto updateGender(String gender) {
    return new PlayerUpdateRequestDto(null, gender, null, null, null, null);
  }

  public static PlayerUpdateRequestDto updateRole(String role) {
    return new PlayerUpdateRequestDto(null, null, null, null, role, null);
  }
}
