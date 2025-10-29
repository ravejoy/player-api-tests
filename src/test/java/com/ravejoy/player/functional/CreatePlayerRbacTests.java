package com.ravejoy.player.functional;

import static com.ravejoy.player.http.StatusCode.FORBIDDEN;

import com.ravejoy.player.assertions.ResponseAsserts;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.*;
import io.qameta.allure.*;
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
            new Object[] {Editor.ADMIN, Role.ADMIN.value(), FORBIDDEN},
            new Object[] {Editor.USER, Role.USER.value(), FORBIDDEN},
            new Object[] {Editor.USER, Role.ADMIN.value(), FORBIDDEN})
        .toArray(Object[][]::new);
  }

  @Description("RBAC matrix for editor vs target role; expects 403 for forbidden operations")
  @Test(
      dataProvider = "rbacMatrix",
      groups = {Groups.FUNCTIONAL, Groups.RBAC})
  public void rbacRestrictions(Editor editor, String targetRole, int expectedStatus) {
    var steps = new PlayerSteps();

    var resp = steps.createAs(editor, targetRole);

    ResponseAsserts.assertStatus(resp, expectedStatus);
    ResponseAsserts.assertJsonOrEmpty(resp);
  }
}
