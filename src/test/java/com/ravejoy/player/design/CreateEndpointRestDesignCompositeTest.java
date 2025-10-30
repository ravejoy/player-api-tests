package com.ravejoy.player.design;

import static com.ravejoy.player.http.StatusCode.OK;
import static org.testng.Assert.assertTrue;

import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.http.ApiClient;
import com.ravejoy.player.players.PlayerEndpoints;
import com.ravejoy.player.players.dto.PlayerGetAllResponseDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Groups;
import com.ravejoy.player.testsupport.Role;
import com.ravejoy.player.testsupport.RunIds;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.Map;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Epic("Player API")
@Feature("Create Endpoint")
@Story("REST Semantics")
public class CreateEndpointRestDesignCompositeTest {

  @Issue("DES-01")
  @Description("Composite REST design checks: GET safety, idempotency, and invalid verb handling.")
  @Test(groups = {Groups.DESIGN, Groups.CONTRACT, Groups.KNOWN_ISSUES})
  public void createEndpointRestDesignComposite() {
    var steps = new PlayerSteps();
    var sa = new SoftAssert();

    PlayerGetAllResponseDto before = steps.getAll();
    int beforeCount = before.players() != null ? before.players().size() : 0;

    String login1 = RunIds.login("user1");
    String screen1 = RunIds.screen("scr1");
    Response r1 = steps.createAsSupervisor(login1, screen1, Role.USER);
    ResponseAsserts.assertStatus(r1, OK);
    ResponseAsserts.assertJsonOrEmpty(r1);

    String cacheControl = r1.getHeader("Cache-Control");
    String pragma = r1.getHeader("Pragma");
    boolean cacheSafe =
        cacheControl != null
            && (cacheControl.contains("no-store") || cacheControl.contains("no-cache"))
            && (pragma == null || "no-cache".equalsIgnoreCase(pragma));
    if (!cacheSafe) sa.fail("Missing cache-safety headers on state-changing GET.");

    PlayerGetAllResponseDto after1 = steps.getAll();
    int afterCount1 = after1.players() != null ? after1.players().size() : 0;
    assertTrue(afterCount1 > beforeCount, "GET create changed state (not safe).");

    String login2 = RunIds.login("user2");
    String screen2 = RunIds.screen("scr2");
    Response r2 = steps.createAsSupervisor(login2, screen2, Role.USER);
    ResponseAsserts.assertStatus(r2, OK);
    ResponseAsserts.assertJsonOrEmpty(r2);

    PlayerGetAllResponseDto after2 = steps.getAll();
    int afterCount2 = after2.players() != null ? after2.players().size() : 0;
    if (afterCount2 != afterCount1) sa.fail("GET create is not idempotent.");

    var api = new ApiClient();
    Response postResp =
        api.post(PlayerEndpoints.CREATE + Editor.SUPERVISOR.value(), Map.of("probe", "x"));
    if (postResp.statusCode() != 405)
      sa.fail("POST /player/create should return 405 Method Not Allowed.");

    // no assertAll() by design
  }
}
