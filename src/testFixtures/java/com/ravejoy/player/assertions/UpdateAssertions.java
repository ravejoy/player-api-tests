package com.ravejoy.player.assertions;

import com.ravejoy.player.players.dto.PlayerGetByPlayerIdResponseDto;
import com.ravejoy.player.players.dto.PlayerUpdateResponseDto;
import com.ravejoy.player.testsupport.Role;
import org.testng.asserts.SoftAssert;

public final class UpdateAssertions {
  private UpdateAssertions() {}

  public static void assertUpdatedMatches(
      SoftAssert sa,
      PlayerUpdateResponseDto updated,
      String expectedLogin,
      String expectedScreen,
      int expectedAge,
      String expectedGender,
      Role expectedRole) {
    sa.assertTrue(updated.id() > 0, "id>0");
    sa.assertEquals(updated.login(), expectedLogin, "login");
    sa.assertEquals(updated.screenName(), expectedScreen, "screenName");
    sa.assertEquals(updated.age(), expectedAge, "age");
    sa.assertEquals(updated.gender(), expectedGender, "gender");
    sa.assertEquals(updated.role(), expectedRole.value(), "role");
  }

  public static void assertFetchedMatches(
      SoftAssert sa,
      PlayerGetByPlayerIdResponseDto fetched,
      String expectedLogin,
      String expectedScreen,
      int expectedAge,
      String expectedGender,
      Role expectedRole) {
    sa.assertTrue(fetched.id() > 0, "id>0");
    sa.assertEquals(fetched.login(), expectedLogin, "login");
    sa.assertEquals(fetched.screenName(), expectedScreen, "screenName");
    sa.assertEquals(fetched.age(), expectedAge, "age");
    sa.assertEquals(fetched.gender(), expectedGender, "gender");
    sa.assertEquals(fetched.role(), expectedRole.value(), "role");
  }
}
