package com.ravejoy.player.functional.createEndpoint;

import static com.ravejoy.player.http.StatusCode.FORBIDDEN;

import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.data.model.PlayerCreateData;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Gender;
import com.ravejoy.player.testsupport.Groups;
import com.ravejoy.player.testsupport.Password;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.util.stream.Stream;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Epic("Player API")
@Feature("Create")
@Story("RBAC restrictions")
public class CreatePlayerRbacTests {

  @DataProvider(name = "rbacMatrix", parallel = true)
  public Object[][] rbacMatrix() {
    return Stream.of(
            new Object[] {Editor.ADMIN, "admin", FORBIDDEN},
            new Object[] {Editor.USER, "user", FORBIDDEN},
            new Object[] {Editor.USER, "admin", FORBIDDEN})
        .toArray(Object[][]::new);
  }

  @Description("RBAC matrix for editor vs target role; expects 403 for forbidden operations")
  @Test(
      dataProvider = "rbacMatrix",
      groups = {Groups.FUNCTIONAL, Groups.RBAC})
  public void rbacRestrictions(Editor editor, String targetRole, int expectedStatus) {
    var steps = new PlayerSteps();

    var data =
        new PlayerCreateData(
            com.ravejoy.player.testsupport.RunIds.login(targetRole),
            com.ravejoy.player.testsupport.RunIds.screen(targetRole),
            targetRole,
            24,
            Gender.MALE,
            Password.VALID);

    var resp = steps.createAs(editor, data);

    ResponseAsserts.assertStatus(resp, expectedStatus);
    ResponseAsserts.assertJsonOrEmpty(resp);
  }
}
