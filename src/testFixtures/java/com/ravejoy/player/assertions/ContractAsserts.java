package com.ravejoy.player.assertions;

import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import org.testng.asserts.SoftAssert;

public final class ContractAsserts {

  private ContractAsserts() {}

  public static void assertCreateContractShape(SoftAssert sa, PlayerCreateResponseDto body) {
    sa.assertTrue(body.id() > 0, "id should be positive long");
    sa.assertNotNull(body.login(), "login should be present");
    sa.assertNotNull(body.screenName(), "screenName should be present");
    sa.assertNotNull(body.role(), "role should be present");
    sa.assertNotNull(body.gender(), "gender should be present");
    sa.assertTrue(body.age() > 0, "age should be positive (int32)");
  }
}
