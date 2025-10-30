package com.ravejoy.player.functional.getEndpoint;

import static com.ravejoy.player.http.StatusCode.BAD_REQUEST;
import static com.ravejoy.player.http.StatusCode.NOT_FOUND;
import static com.ravejoy.player.http.StatusCode.OK;

import com.ravejoy.player.assertions.ContractAsserts;
import com.ravejoy.player.assertions.PlayerAsserts;
import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.data.model.PlayerCreateData;
import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.players.dto.PlayerGetByPlayerIdResponseDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.*;
import com.ravejoy.player.testsupport.helper.Jsons;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Epic("Player API")
@Feature("Get By Id")
@Story("Fetch player by id")
public class GetPlayerByIdTests {

  private static PlayerCreateData newUserData() {
    return new PlayerCreateData(
        RunIds.login(Role.USER.value()),
        RunIds.screen(Role.USER.value()),
        Role.USER.value(),
        24,
        Gender.MALE,
        Password.VALID);
  }

  @Description(
      "Create → GET by id → fields match and contract holds (robust to missing Content-Type)")
  @Test(groups = {Groups.FUNCTIONAL, Groups.CONTRACT})
  public void supervisorGetsExistingPlayer() {
    var steps = new PlayerSteps();
    var data = newUserData();

    Response createResp = steps.createAs(Editor.SUPERVISOR, data);
    ResponseAsserts.assertOkJson(createResp);

    long id = createResp.as(PlayerCreateResponseDto.class).id();

    Response getResp = steps.getByIdRaw(id);
    ResponseAsserts.assertStatus(getResp, OK);
    ResponseAsserts.assertJsonOrEmpty(getResp);

    PlayerGetByPlayerIdResponseDto got = Jsons.toDto(getResp, PlayerGetByPlayerIdResponseDto.class);

    var sa = new SoftAssert();
    ContractAsserts.assertGetByIdContractShape(sa, got);
    PlayerAsserts.assertFetchedMatches(sa, got, data.login(), data.screenName(), Role.USER);
    sa.assertAll();
  }

  @DataProvider(name = "badIds", parallel = true)
  public Object[][] badIds() {
    return new Object[][] {{0L}, {-1L}};
  }

  @Issue("API-02")
  @Description("Invalid ids (0, -1) should return 400 Bad Request")
  @Test(
      dataProvider = "badIds",
      groups = {Groups.KNOWN_ISSUES, Groups.CONTRACT})
  public void getByIdBadRequest(long badId) {
    var steps = new PlayerSteps();
    Response resp = steps.getByIdRaw(badId);
    ResponseAsserts.assertStatus(resp, BAD_REQUEST);
    ResponseAsserts.assertJsonOrEmpty(resp);
  }

  @Issue("API-02")
  @Description("Unknown id should return 404 Not Found")
  @Test(groups = {Groups.KNOWN_ISSUES, Groups.CONTRACT})
  public void getByIdNotFound() {
    var steps = new PlayerSteps();
    long unknownId = Math.abs(new java.util.Random().nextLong()) + 1_000_000_000_000L;

    Response resp = steps.getByIdRaw(unknownId);
    ResponseAsserts.assertStatus(resp, NOT_FOUND);
    ResponseAsserts.assertJsonOrEmpty(resp);
  }
}
