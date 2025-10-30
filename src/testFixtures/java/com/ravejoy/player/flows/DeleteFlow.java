package com.ravejoy.player.flows;

import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Gender;
import com.ravejoy.player.testsupport.Password;
import com.ravejoy.player.testsupport.Role;
import com.ravejoy.player.testsupport.RunIds;
import io.restassured.response.Response;

public final class DeleteFlow {
  private final PlayerSteps steps;

  public DeleteFlow(PlayerSteps steps) {
    this.steps = steps;
  }

  public PlayerCreateResponseDto createUserForDeletion(Role role) {
    String login = RunIds.login(role.value());
    String screen = RunIds.screen(role.value());
    return steps.create(Editor.SUPERVISOR.value(), login, screen, role.value(), 24, Gender.MALE, Password.VALID);
  }

  public Response deleteAs(Editor editor, long id) {
    return steps.deleteAndReturn(editor, id);
  }

  public Response getByIdRaw(long id) {
    return steps.getByIdRaw(id);
  }

  public boolean existsInListById(long id) {
    var all = steps.getAll();
    return all != null && all.players() != null && all.players().stream().anyMatch(p -> p.id() == id);
  }
}
