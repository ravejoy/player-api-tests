package com.ravejoy.player.design;

import static com.ravejoy.player.http.StatusCode.OK;

import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.data.model.PlayerCreateData;
import com.ravejoy.player.players.PlayerEndpoints;
import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.players.dto.PlayerGetByPlayerIdResponseDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.*;
import com.ravejoy.player.testsupport.helper.Jsons;
import io.qameta.allure.*;
import io.restassured.response.Response;
import java.util.Map;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Epic("Player API")
@Feature("Get By Id")
@Story("REST design semantics")
public class GetPlayerDesignTests {

  private static PlayerCreateData data() {
    return new PlayerCreateData(
        RunIds.login(Role.USER.value()),
        RunIds.screen(Role.USER.value()),
        Role.USER.value(),
        24,
        Gender.MALE,
        Password.VALID);
  }

  @Issue("DES-01")
  @Issue("DES-02")
  @Description("Design: read must be safe/idempotent, support GET semantics, and be cacheable")
  @Test(groups = {Groups.DESIGN, Groups.KNOWN_ISSUES})
  public void getByIdDesignSemantics() {
    var steps = new PlayerSteps();
    var d = data();

    Response createResp = steps.createAs(Editor.SUPERVISOR, d);
    ResponseAsserts.assertOkJson(createResp);
    long id = createResp.as(PlayerCreateResponseDto.class).id();

    Response r1 = steps.getByIdRaw(id);
    ResponseAsserts.assertStatus(r1, OK);
    ResponseAsserts.assertJsonOrEmpty(r1);
    var dto1 = Jsons.toDto(r1, PlayerGetByPlayerIdResponseDto.class);

    Response r2 = steps.getByIdRaw(id);
    ResponseAsserts.assertStatus(r2, OK);
    ResponseAsserts.assertJsonOrEmpty(r2);
    var dto2 = Jsons.toDto(r2, PlayerGetByPlayerIdResponseDto.class);

    var sa = new SoftAssert();

    sa.assertEquals(dto1.id(), dto2.id(), "Idempotent read: same id");
    sa.assertEquals(dto1.login(), dto2.login(), "Idempotent read: same login");
    sa.assertEquals(dto1.screenName(), dto2.screenName(), "Idempotent read: same screenName");
    sa.assertEquals(dto1.role(), dto2.role(), "Idempotent read: same role");
    sa.assertEquals(dto1.age(), dto2.age(), "Idempotent read: same age");
    sa.assertEquals(dto1.gender(), dto2.gender(), "Idempotent read: same gender");

    var cacheCtl = r1.getHeader("Cache-Control");
    sa.assertNotNull(cacheCtl, "Read responses should expose Cache-Control");

    var api = new com.ravejoy.player.http.ApiClient();
    Response wrongMethod = api.get(PlayerEndpoints.GET, Map.of("playerId", id));
    sa.assertNotEquals(
        wrongMethod.statusCode(), OK, "Design: GET /player/get?playerId=... should not be 200 OK");

    sa.assertAll();
  }
}
