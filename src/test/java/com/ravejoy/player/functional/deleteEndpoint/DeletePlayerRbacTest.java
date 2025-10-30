package com.ravejoy.player.functional.deleteEndpoint;

import static com.ravejoy.player.assertions.DeleteAssertions.assertForbidden;

import com.ravejoy.player.flows.DeleteFlow;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.*;
import com.ravejoy.player.dataproviders.DeleteDataProviders;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

@Epic("Player API")
@Feature("Delete")
@Story("RBAC")
public class DeletePlayerRbacTest {

  @Description("Delete is allowed only for supervisor (admin must NOT delete supervisor)")
  @Test(
      dataProvider = "rbacAllowedMatrix",
      dataProviderClass = DeleteDataProviders.class,
      groups = {Groups.FUNCTIONAL, Groups.RBAC}
  )
  public void deleteAllowed(Editor editor, Role targetRole) {
    var flow = new DeleteFlow(new PlayerSteps());
    var created = flow.createUserForDeletion(targetRole);

    Response resp = flow.deleteAs(editor, created.id());
    com.ravejoy.player.assertions.ResponseAsserts.assertOkOrNoContentJsonOrEmpty(resp);
  }

  @Issue("RBAC-02")
  @Description("[KNOWN ISSUE] User should NOT be able to delete, but API returns 204 instead of 403")
  @Test(
      dataProvider = "rbacUserNegativeMatrix",
      dataProviderClass = DeleteDataProviders.class,
      groups = {Groups.KNOWN_ISSUES, Groups.RBAC}
  )
  public void deleteForbiddenForUser(Editor editor, Role targetRole) {
    var flow = new DeleteFlow(new PlayerSteps());
    var created = flow.createUserForDeletion(targetRole);

    Response resp = flow.deleteAs(editor, created.id());

    assertForbidden(resp);
  }
}
