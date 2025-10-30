package com.ravejoy.player.functional.updateEndpoint;

import static org.testng.Assert.assertEquals;

import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.dataproviders.UpdateDataProviders;
import com.ravejoy.player.flows.UpdateFlow;
import com.ravejoy.player.players.dto.PlayerUpdateRequestDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Groups;
import com.ravejoy.player.testsupport.Role;
import com.ravejoy.player.testsupport.RunIds;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;

@Epic("Player API")
@Feature("Update")
@Story("Happy path")
public final class UpdatePlayerHappyPathTest {

  @Test(
      dataProvider = "rolesToCreate",
      dataProviderClass = UpdateDataProviders.class,
      groups = {Groups.FUNCTIONAL})
  public void supervisorUpdatesPlayer(Role role) {
    var flow = new UpdateFlow(new PlayerSteps());
    var created = flow.create(role);

    String newLogin = RunIds.login("upd_" + created.id());
    String newScreen = RunIds.screen("upd_" + created.id());

    var dto = new PlayerUpdateRequestDto(25, "male", newLogin, "qwerty12", null, newScreen);

    Response r = flow.updateRaw(Editor.SUPERVISOR, created.id(), dto);
    ResponseAsserts.assertOkOrNoContentJsonOrEmpty(r);

    var after = flow.get(created.id());
    assertEquals(after.login(), newLogin);
    assertEquals(after.screenName(), newScreen);
    assertEquals(after.age(), Integer.valueOf(25));
    assertEquals(after.gender(), "male");
  }
}
