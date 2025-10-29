package com.ravejoy.player.functional;

import static com.ravejoy.player.http.StatusCode.FORBIDDEN;
import static org.testng.Assert.assertEquals;

import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Gender;
import com.ravejoy.player.testsupport.Groups;
import com.ravejoy.player.testsupport.Password;
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
public class CreatePlayerRbacTests {

  @DataProvider(name = "rbacMatrix", parallel = true)
  public Object[][] rbacMatrix() {
    return Stream.of(
            new Object[] {Editor.ADMIN, Role.ADMIN.value(), FORBIDDEN},
            new Object[] {Editor.USER, Role.USER.value(), FORBIDDEN},
            new Object[] {Editor.USER, Role.ADMIN.value(), FORBIDDEN})
        .toArray(Object[][]::new);
  }

  @Story("RBAC restrictions")
  @Description("RBAC matrix for editor vs target role; expects 403 Forbidden for forbidden ops")
  @Test(
      dataProvider = "rbacMatrix",
      groups = {Groups.FUNCTIONAL, Groups.RBAC})
  public void rbacRestrictions(Editor editor, String targetRole, int expectedStatus) {
    var steps = new PlayerSteps();

    var resp =
        steps.createRaw(
            editor.value(),
            RunIds.login(targetRole),
            RunIds.screen(targetRole),
            targetRole,
            25,
            Gender.MALE,
            Password.VALID);

    assertEquals(resp.statusCode(), expectedStatus, "RBAC mismatch for editor=" + editor);
  }
}
