package com.ravejoy.player.functional.createEndpoint;

import com.ravejoy.player.assertions.PlayerAsserts;
import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.data.model.PlayerCreateData;
import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Gender;
import com.ravejoy.player.testsupport.Groups;
import com.ravejoy.player.testsupport.Password;
import com.ravejoy.player.testsupport.Role;
import com.ravejoy.player.testsupport.RunIds;
import com.ravejoy.player.testsupport.helper.PlayerLookup;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.stream.Stream;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Epic("Player API")
@Feature("Create")
@Story("Create player")
public class CreatePlayerTests {

  @DataProvider(name = "supervisorMatrix", parallel = true)
  public Object[][] supervisorMatrix() {
    return Stream.of(new Object[] {Role.USER}, new Object[] {Role.ADMIN}).toArray(Object[][]::new);
  }

  @Description("Supervisor creates USER/ADMIN and the player is persisted (verified via lookup)")
  @Test(
      dataProvider = "supervisorMatrix",
      groups = {Groups.FUNCTIONAL})
  public void supervisorCreatesPlayer(Role targetRole) {
    var steps = new PlayerSteps();
    final String login = RunIds.login(targetRole.value());
    final String screen = RunIds.screen("scr");
    var data =
        new PlayerCreateData(login, screen, targetRole.value(), 24, Gender.MALE, Password.VALID);

    Response createResp = steps.createAs(Editor.SUPERVISOR, data);
    ResponseAsserts.assertOkJson(createResp);

    var sa = new SoftAssert();
    var created = createResp.as(PlayerCreateResponseDto.class);
    PlayerAsserts.assertCreatedMatches(sa, created, login, screen, targetRole);

    var fetched = PlayerLookup.getById(created.id());
    PlayerAsserts.assertFetchedMatches(sa, fetched, login, screen, targetRole);
    PlayerAsserts.assertSameEntity(sa, created, fetched);

    sa.assertTrue(
        PlayerLookup.existsByScreenName(screen), "Persisted list should contain screenName");
    sa.assertAll();
  }

  @Issue("RBAC-01")
  @Description("[KNOWN ISSUE] Admin creates USER: expected 200 OK, currently returns 403 Forbidden")
  @Test(groups = {Groups.KNOWN_ISSUES})
  public void adminCreatesUser_knownIssue() {
    var steps = new PlayerSteps();
    final String login = RunIds.login(Role.USER.value());
    final String screen = RunIds.screen("scr");
    var data =
        new PlayerCreateData(login, screen, Role.USER.value(), 24, Gender.MALE, Password.VALID);

    var resp = steps.createAs(Editor.ADMIN, data);

    ResponseAsserts.assertStatus(resp, com.ravejoy.player.http.StatusCode.OK);
    ResponseAsserts.assertJsonOrEmpty(resp);
  }
}
