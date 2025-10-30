package com.ravejoy.player.functional.updateEndpoint;

import static com.ravejoy.player.http.StatusCode.BAD_REQUEST;

import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.dataproviders.UpdateDataProviders;
import com.ravejoy.player.flows.UpdateFlow;
import com.ravejoy.player.players.dto.PlayerUpdateRequestDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Groups;
import com.ravejoy.player.testsupport.Role;
import com.ravejoy.player.testsupport.RunIds;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

@Epic("Player API")
@Feature("Update")
@Story("Validation")
public final class UpdatePlayerValidationTest {

  @Issue("VAL-04")
  @Description("[KNOWN ISSUE] Update accepts invalid ages; expected 400, returns 200")
  @Test(
      dataProvider = "invalidAges",
      dataProviderClass = UpdateDataProviders.class,
      groups = {Groups.KNOWN_ISSUES, Groups.CONTRACT})
  public void invalidAgesAreRejected(int badAge) {
    var flow = new UpdateFlow(new PlayerSteps());
    var created = flow.create(Role.USER);

    var dto =
        new PlayerUpdateRequestDto(
            badAge,
            "male",
            RunIds.login("upd_" + created.id()),
            "qwerty12",
            null,
            RunIds.screen("upd_" + created.id()));

    Response r = flow.updateRaw(Editor.SUPERVISOR, created.id(), dto);
    ResponseAsserts.assertStatus(r, BAD_REQUEST);
    ResponseAsserts.assertJsonOrEmpty(r);
  }
}
