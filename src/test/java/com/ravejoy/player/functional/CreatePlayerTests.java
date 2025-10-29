package com.ravejoy.player.functional;

import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.*;
import com.ravejoy.player.testsupport.helper.PlayerLookup;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.stream.Stream;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Story("Create player")
public class CreatePlayerTests {

  @DataProvider
  public Object[][] supervisorMatrix() {
    return Stream.of(
            new Object[] {Role.USER},
            new Object[] {Role.ADMIN})
        .toArray(Object[][]::new);
  }

  @Description("Supervisor creates USER/ADMIN and player appears in system")
  @Test(dataProvider = "supervisorMatrix", groups = {Groups.FUNCTIONAL})
  public void supervisorCreatesPlayer(Role targetRole) {
    var steps = new PlayerSteps();

    var login = RunIds.login(targetRole.value());
    var screen = RunIds.screen("scr");

    int before = PlayerLookup.countByPrefix(RunIds.prefix());

    Response createResp =
        steps.createRaw(
            Editor.SUPERVISOR.value(), login, screen, targetRole.value(), 24, Gender.MALE, Password.VALID);

    var created = createResp.as(PlayerCreateResponseDto.class);

    var sa = new SoftAssert();
    sa.assertTrue(created != null && created.id() > 0L, "Created id should be positive");

    var fetched = PlayerLookup.getById(created.id());
    sa.assertEquals(fetched.login(), login, "Login mismatch");

    int after = PlayerLookup.countByPrefix(RunIds.prefix());
    sa.assertEquals(after, before + 1, "Players count with prefix should increase by 1");

    sa.assertAll();
  }

  @Issue("@Issue(\"RBAC-01\")")
  @Description("[KNOWN ISSUE] Admin creates USER expected 200, actual 403")
  @Test(groups = {Groups.KNOWN_ISSUES})
  public void adminCreatesUser() {
    var steps = new PlayerSteps();

    var login = RunIds.login("user");
    var screen = RunIds.screen("scr");

    var resp =
        steps.createRaw(
            Editor.ADMIN.value(), login, screen, Role.USER.value(), 24, Gender.MALE, Password.VALID);

    org.testng.Assert.assertEquals(resp.statusCode(), 200);
  }
}
