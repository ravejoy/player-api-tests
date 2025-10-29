package com.ravejoy.player.assertions;

import static org.testng.Assert.assertNotNull;

import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.testsupport.Role;
import org.testng.asserts.SoftAssert;

public final class PlayerAsserts {
  private PlayerAsserts() {}

  public static void assertCreatedMatches(
      SoftAssert sa,
      PlayerCreateResponseDto dto,
      String expectedLogin,
      String expectedScreen,
      Role expectedRole) {

    assertNotNull(dto, "Response body should not be null");
    sa.assertTrue(dto.id() > 0L, "Player ID should be positive");
    sa.assertEquals(dto.login(), expectedLogin, "Login mismatch");
    sa.assertEquals(dto.screenName(), expectedScreen, "Screen name mismatch");
    sa.assertEquals(dto.role(), expectedRole.value(), "Role mismatch");
  }
}
