package com.ravejoy.player.functional.deleteEndpoint;

import static com.ravejoy.player.http.StatusCode.BAD_REQUEST;
import static com.ravejoy.player.http.StatusCode.NOT_FOUND;

import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.flows.DeleteFlow;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Groups;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Epic("Player API")
@Feature("Delete")
@Story("Contract & Validation")
public class DeletePlayerContractTest {

  @DataProvider(name = "badIds", parallel = true)
  public Object[][] badIds() {
    return new Object[][] {{0L}, {-1L}};
  }

  @Description("Invalid playerId (0, -1) should return 400 Bad Request")
  @Test(
      dataProvider = "badIds",
      groups = {Groups.KNOWN_ISSUES, Groups.CONTRACT})
  public void deleteBadRequest(long badId) {
    var flow = new DeleteFlow(new PlayerSteps());
    Response r = flow.deleteAs(Editor.SUPERVISOR, badId);

    ResponseAsserts.assertStatus(r, BAD_REQUEST);
    ResponseAsserts.assertJsonOrEmpty(r);
  }

  @Issue("API-03")
  @Description("Unknown playerId should return 404 Not Found")
  @Test(groups = {Groups.KNOWN_ISSUES, Groups.CONTRACT})
  public void deleteUnknownId() {
    var flow = new DeleteFlow(new PlayerSteps());
    long unknown = Math.abs(System.nanoTime()) + 1_000_000_000L;

    Response r = flow.deleteAs(Editor.SUPERVISOR, unknown);

    ResponseAsserts.assertStatus(r, NOT_FOUND);
    ResponseAsserts.assertJsonOrEmpty(r);
  }
}
