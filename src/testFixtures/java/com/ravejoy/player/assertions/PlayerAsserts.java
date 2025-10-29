package com.ravejoy.player.assertions;

import static org.testng.Assert.assertNotNull;

import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.players.dto.PlayerGetByPlayerIdResponseDto;
import com.ravejoy.player.testsupport.Role;
import org.testng.asserts.SoftAssert;

public final class PlayerAsserts {
  private PlayerAsserts() {}

  public static void assertCreatedMatches(
      SoftAssert sa,
      PlayerCreateResponseDto dto,
      String expectedLogin,
      String ignoredScreen,
      Role ignoredRole) {

    assertNotNull(dto, "Response body must not be null");
    sa.assertTrue(dto.id() > 0L, "id must be > 0");
    sa.assertEquals(dto.login(), expectedLogin, "login");
  }

  public static void assertFetchedMatches(
      SoftAssert sa,
      PlayerGetByPlayerIdResponseDto dto,
      String expectedLogin,
      String expectedScreen,
      Role expectedRole) {

    assertNotNull(dto, "Fetched body must not be null");
    sa.assertTrue(dto.id() > 0L, "id must be > 0");
    sa.assertEquals(dto.login(), expectedLogin, "login");
    sa.assertEquals(dto.screenName(), expectedScreen, "screenName");
    sa.assertEquals(dto.role(), expectedRole.value(), "role");
  }

  public static void assertSameEntity(
      SoftAssert sa, PlayerCreateResponseDto created, PlayerGetByPlayerIdResponseDto fetched) {

    assertNotNull(created, "created");
    assertNotNull(fetched, "fetched");
    sa.assertEquals(fetched.id(), created.id(), "id");
    sa.assertEquals(fetched.login(), created.login(), "login");
  }
}
