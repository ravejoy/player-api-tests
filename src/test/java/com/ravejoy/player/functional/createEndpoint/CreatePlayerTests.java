package com.ravejoy.player.functional.createEndpoint;

import com.ravejoy.player.assertions.PlayerAsserts;
import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.data.model.PlayerCreateData;
import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.players.dto.PlayerGetByPlayerIdResponseDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Gender;
import com.ravejoy.player.testsupport.Groups;
import com.ravejoy.player.testsupport.Password;
import com.ravejoy.player.testsupport.Role;
import com.ravejoy.player.testsupport.RunIds;
import com.ravejoy.player.testsupport.helper.Jsons;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Epic("Player API")
@Feature("Create")
@Story("Create player")
public class CreatePlayerTests {

  @DataProvider(name = "supervisorMatrix", parallel = true)
  public Object[][] supervisorMatrix() {
    return new Object[][] {{Role.USER}, {Role.ADMIN}};
  }

  @Description("Supervisor creates USER/ADMIN and the player is persisted (verified via GET)")
  @Test(
      dataProvider = "supervisorMatrix",
      groups = {Groups.FUNCTIONAL})
  public void supervisorCreatesPlayer(Role targetRole) throws InterruptedException {
    var steps = new PlayerSteps();

    var data =
        new PlayerCreateData(
            RunIds.login(targetRole.value()),
            RunIds.screen(targetRole.value()),
            targetRole.value(),
            24,
            Gender.MALE,
            Password.VALID);

    Response createResp = steps.createAsSupervisor(data.login(), data.screenName(), targetRole);
    ResponseAsserts.assertOkJson(createResp);

    var created = createResp.as(PlayerCreateResponseDto.class);
    Assert.assertTrue(created.id() > 0, "id should be positive");

    Response getResp = steps.getByIdRaw(created.id());
    ResponseAsserts.assertStatus(getResp, com.ravejoy.player.http.StatusCode.OK);

    PlayerGetByPlayerIdResponseDto fetched =
        Jsons.toDto(getResp, PlayerGetByPlayerIdResponseDto.class);

    if (fetched == null) {
      Thread.sleep(300);
      getResp = steps.getByIdRaw(created.id());
      ResponseAsserts.assertStatus(getResp, com.ravejoy.player.http.StatusCode.OK);
      fetched = Jsons.toDto(getResp, PlayerGetByPlayerIdResponseDto.class);
    }

    Assert.assertNotNull(fetched, "Entity must appear via GET after create");

    var sa = new SoftAssert();
    PlayerAsserts.assertFetchedMatches(sa, fetched, data.login(), data.screenName(), targetRole);
    sa.assertAll();
  }

  @Issue("RBAC-01")
  @Description("[KNOWN ISSUE] Admin creates USER: expected 200 OK, currently returns 403 Forbidden")
  @Test(groups = {Groups.KNOWN_ISSUES})
  public void adminCreatesUser_knownIssue() {
    var steps = new PlayerSteps();
    final String login = RunIds.login(Role.USER.value());
    final String screen = RunIds.screen(Role.USER.value());

    var resp = steps.createAsAdmin(login, screen, Role.USER);

    ResponseAsserts.assertStatus(resp, com.ravejoy.player.http.StatusCode.OK);
    ResponseAsserts.assertJsonOrEmpty(resp);
  }
}
