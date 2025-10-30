package com.ravejoy.player.functional.create;

import static com.ravejoy.player.http.StatusCode.BAD_REQUEST;
import static com.ravejoy.player.http.StatusCode.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.ravejoy.player.assertions.ContractAsserts;
import com.ravejoy.player.assertions.PlayerAsserts;
import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Gender;
import com.ravejoy.player.testsupport.Groups;
import com.ravejoy.player.testsupport.Role;
import com.ravejoy.player.testsupport.RunIds;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Epic("Player API")
@Feature("Create")
@Story("Contract")
public class CreatePlayerContractTests {

  @Description(
      "CREATE minimal contract: 200 + JSON + id>0, echoes login (baseline that should pass now)")
  @Test(groups = {Groups.FUNCTIONAL, Groups.CONTRACT})
  public void supervisorCreatesUserContractMinimal() {
    var steps = new PlayerSteps();
    final String login = RunIds.login(Role.USER.value());
    final String screen = RunIds.screen("scr");

    Response resp = steps.createAsSupervisor(login, screen, Role.USER);

    ResponseAsserts.assertOkJson(resp);

    var body = resp.as(PlayerCreateResponseDto.class);
    assertTrue(body.id() > 0, "id should be positive");
    assertEquals(body.login(), login, "login should echo request");
  }

  @Issue("API-01")
  @Description(
      "[KNOWN ISSUE] CREATE strict contract shape per Swagger: non-null typed fields. Returns many nulls.")
  @Test(groups = {Groups.KNOWN_ISSUES, Groups.CONTRACT})
  public void supervisorCreatesUserContractStrict() {
    var steps = new PlayerSteps();
    final String login = RunIds.login(Role.USER.value());
    final String screen = RunIds.screen("scr");

    Response resp = steps.createAsSupervisor(login, screen, Role.USER);
    ResponseAsserts.assertStatus(resp, OK);
    ResponseAsserts.assertJsonOrEmpty(resp);

    var sa = new SoftAssert();
    var body = resp.as(PlayerCreateResponseDto.class);

    ContractAsserts.assertCreateContractShape(sa, body);
    PlayerAsserts.assertCreatedMatches(sa, body, login, screen, Role.USER);

    sa.assertAll();
  }

  @Issue("VAL-01")
  @Description(
      "[KNOWN ISSUE] Password is required by task description; empty password must be rejected.")
  @Test(groups = {Groups.KNOWN_ISSUES, Groups.CONTRACT})
  public void supervisorCreatesUserWithEmptyPassword() {
    var steps = new PlayerSteps();
    final String login = RunIds.login(Role.USER.value());
    final String screen = RunIds.screen("scr");

    Response resp =
        steps.createRaw(
            Editor.SUPERVISOR.value(), login, screen, Role.USER.value(), 24, Gender.MALE, "");

    ResponseAsserts.assertStatus(resp, BAD_REQUEST);
    ResponseAsserts.assertJsonOrEmpty(resp);
  }
}
