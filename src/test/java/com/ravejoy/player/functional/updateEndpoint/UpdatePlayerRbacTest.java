package com.ravejoy.player.functional.updateEndpoint;

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
@Story("RBAC")
public final class UpdatePlayerRbacTest {

  @Test(
      dataProvider = "rbacEditors",
      dataProviderClass = UpdateDataProviders.class,
      groups = {Groups.FUNCTIONAL, Groups.RBAC})
  public void rbacMatrix(Editor editor, Role targetRole, int expectedStatus) {
    var flow = new UpdateFlow(new PlayerSteps());
    var created = flow.create(targetRole);

    var dto =
        new PlayerUpdateRequestDto(
            25,
            "male",
            RunIds.login("upd_" + created.id()),
            "qwerty12",
            null,
            RunIds.screen("upd_" + created.id()));

    Response r = flow.updateRaw(editor, created.id(), dto);
    ResponseAsserts.assertStatus(r, expectedStatus);
    ResponseAsserts.assertJsonOrEmpty(r);
  }

  @Issue("RBAC-03")
  @Description("[KNOWN ISSUE] Admin cannot update USER (expected 200, gets 403)")
  @Test(groups = {Groups.KNOWN_ISSUES, Groups.RBAC})
  public void adminShouldUpdateUser_butForbidden() {
    var flow = new UpdateFlow(new PlayerSteps());
    var created = flow.create(Role.USER);

    var dto =
        new PlayerUpdateRequestDto(
            25,
            "male",
            RunIds.login("upd_" + created.id()),
            "qwerty12",
            null,
            RunIds.screen("upd_" + created.id()));

    Response r = flow.updateRaw(Editor.ADMIN, created.id(), dto);

    ResponseAsserts.assertStatus(r, com.ravejoy.player.http.StatusCode.OK);
    ResponseAsserts.assertJsonOrEmpty(r);
  }
}
