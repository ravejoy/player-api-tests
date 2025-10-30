package com.ravejoy.player.flows;

import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.players.dto.PlayerGetByPlayerIdResponseDto;
import com.ravejoy.player.players.dto.PlayerUpdateRequestDto;
import com.ravejoy.player.players.dto.PlayerUpdateResponseDto;
import com.ravejoy.player.steps.PlayerSteps;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Gender;
import com.ravejoy.player.testsupport.Password;
import com.ravejoy.player.testsupport.Role;
import com.ravejoy.player.testsupport.RunIds;
import com.ravejoy.player.testsupport.helper.Jsons;
import io.restassured.response.Response;

public final class UpdateFlow {
  private final PlayerSteps steps;

  public UpdateFlow(PlayerSteps steps) {
    this.steps = steps;
  }

  public PlayerCreateResponseDto create(Role role) {
    String login = RunIds.login(role.value());
    String screen = RunIds.screen(role.value());
    return steps.create(
        Editor.SUPERVISOR.value(), login, screen, role.value(), 24, Gender.MALE, Password.VALID);
  }

  public PlayerUpdateResponseDto update(Editor editor, long id, PlayerUpdateRequestDto dto) {
    return steps.update(editor, id, dto);
  }

  public Response updateRaw(Editor editor, long id, PlayerUpdateRequestDto dto) {
    return steps.updateRaw(editor, id, dto);
  }

  public PlayerGetByPlayerIdResponseDto get(long id) {
    var resp = steps.getByIdRaw(id);
    if (!Jsons.isEmptyBody(resp)) {
      return Jsons.from(resp, PlayerGetByPlayerIdResponseDto.class);
    }

    var all = steps.getAll();
    var hit =
        all.players() == null
            ? null
            : all.players().stream().filter(p -> p.id() == id).findFirst().orElse(null);

    if (hit == null) {
      throw new IllegalStateException("Entity not found in list after update, id=" + id);
    }

    return new PlayerGetByPlayerIdResponseDto(
        hit.age(), hit.gender(), hit.id(), null, null, null, hit.screenName());
  }
}
