package com.ravejoy.player.functional.deleteEndpoint;

import static com.ravejoy.player.assertions.DeleteAssertions.assertDeletedOkOr204Empty;
import static com.ravejoy.player.assertions.DeleteAssertions.assertGetByIdReturnsEmptyBody200;

import com.ravejoy.player.dataproviders.DeleteDataProviders;
import com.ravejoy.player.flows.DeleteFlow;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Groups;
import com.ravejoy.player.testsupport.Role;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Player API")
@Feature("Delete")
@Story("Happy path")
public class DeletePlayerHappyPathTest {

  @Description("Supervisor deletes a USER; entity becomes non-fetchable and disappears from list")
  @Test(dataProvider = "rolesToCreate", dataProviderClass = DeleteDataProviders.class, groups = {Groups.FUNCTIONAL})
  public void supervisorDeletesUser(Role role) {
    var flow = new DeleteFlow(new PlayerSteps());
    var created = flow.createUserForDeletion(role);

    Response del = flow.deleteAs(Editor.SUPERVISOR, created.id());
    assertDeletedOkOr204Empty(del);

    Response after = flow.getByIdRaw(created.id());
    assertGetByIdReturnsEmptyBody200(after);

    boolean listed = flow.existsInListById(created.id());
    Assert.assertFalse(listed, "Deleted id must not be present in list");
  }
}
