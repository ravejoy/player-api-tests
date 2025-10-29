package com.ravejoy.player.functional;

import static com.ravejoy.player.http.StatusCode.OK;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.ravejoy.player.http.ApiClient;
import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Gender;
import com.ravejoy.player.testsupport.Groups;
import com.ravejoy.player.testsupport.HttpHeader;
import com.ravejoy.player.testsupport.Password;
import com.ravejoy.player.testsupport.ResourceTracker;
import com.ravejoy.player.testsupport.Role;
import com.ravejoy.player.testsupport.RunIds;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.util.stream.Stream;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Epic("Player API")
@Feature("Create Player")
public class CreatePlayerPositiveTests {

  @DataProvider(name = "positiveEditors", parallel = true)
  public Object[][] positiveEditors() {
    return Stream.of(Editor.SUPERVISOR, Editor.ADMIN)
        .map(e -> new Object[] {e})
        .toArray(Object[][]::new);
  }

  @Story("Positive flow")
  @Description("Editor creates a valid USER successfully (200 OK)")
  @Test(
      dataProvider = "positiveEditors",
      groups = {Groups.FUNCTIONAL})
  public void editorCanCreateUser(Editor editor) {
    var client = new PlayerClient(new ApiClient());
    var login = RunIds.login("user");
    var screen = RunIds.screen("scr");

    var response =
        client.createRaw(
            editor.value(), login, screen, Role.USER.value(), 24, Gender.MALE, Password.VALID);

    assertEquals(response.statusCode(), OK, "Unexpected status code");

    var created = response.as(PlayerCreateResponseDto.class);

    assertNotNull(created, "Response body should not be null");
    assertTrue(created.id() > 0L, "Player ID should be positive");
    assertEquals(created.login(), login);
    assertEquals(created.screenName(), screen);
    assertEquals(created.role(), Role.USER.value());

    var contentType = response.getHeader(HttpHeader.CONTENT_TYPE);
    assertTrue(
        contentType != null && contentType.toLowerCase().startsWith("application/json"),
        "Content-Type should be application/json");

    ResourceTracker.registerPlayer(created.id());
  }
}
