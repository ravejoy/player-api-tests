package com.ravejoy.player.functional;

import static com.ravejoy.player.http.StatusCode.OK;
import static org.testng.Assert.*;

import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Gender;
import com.ravejoy.player.testsupport.Groups;
import com.ravejoy.player.testsupport.HttpHeader;
import com.ravejoy.player.testsupport.MediaType;
import com.ravejoy.player.testsupport.Password;
import com.ravejoy.player.testsupport.Role;
import com.ravejoy.player.testsupport.RunIds;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.util.stream.Stream;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Story("Positive flow")
public class CreatePlayerPositiveTests {

  @DataProvider
  public Object[][] positiveMatrix() {
    return Stream.of(
            new Object[] {Editor.SUPERVISOR, Role.ADMIN},
            new Object[] {Editor.SUPERVISOR, Role.USER},
            new Object[] {Editor.ADMIN, Role.USER})
        .toArray(Object[][]::new);
  }

  @Description("Supervisor/Admin can create USER; Supervisor can also create ADMIN (200 OK)")
  @Test(
      dataProvider = "positiveMatrix",
      groups = {Groups.FUNCTIONAL})
  public void editorCreatesPlayer_positive(Editor editor, Role targetRole) {
    var steps = new PlayerSteps();

    var login = RunIds.login(targetRole.value());
    var screen = RunIds.screen("scr");

    Response response =
        steps.createRaw(
            editor.value(), login, screen, targetRole.value(), 24, Gender.MALE, Password.VALID);

    assertEquals(response.statusCode(), OK, "Unexpected status code");

    var created = response.as(PlayerCreateResponseDto.class);
    assertNotNull(created, "Response body should not be null");
    assertTrue(created.id() > 0L, "Player ID should be positive");
    assertEquals(created.login(), login);
    assertEquals(created.screenName(), screen);
    assertEquals(created.role(), targetRole.value());

    var contentType = response.getHeader(HttpHeader.CONTENT_TYPE);
    assertTrue(
        contentType != null && contentType.toLowerCase().startsWith(MediaType.APPLICATION_JSON),
        "Content-Type should be application/json");
  }
}
