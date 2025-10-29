// src/testFixtures/java/com/ravejoy/player/steps/PlayerSteps.java
package com.ravejoy.player.steps;

import com.ravejoy.player.http.ApiClient;
import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.players.dto.*;
import com.ravejoy.player.testsupport.Editor;
import com.ravejoy.player.testsupport.Gender;
import com.ravejoy.player.testsupport.Password;
import com.ravejoy.player.testsupport.ResourceTracker;
import com.ravejoy.player.testsupport.Role;
import com.ravejoy.player.testsupport.RunIds;
import io.restassured.response.Response;

public final class PlayerSteps {

  private final PlayerClient client = new PlayerClient(new ApiClient());

  // ---------- CREATE ----------

  /** Raw create (Response). Auto-registers ID if present. */
  public Response createRaw(
      String editor,
      String login,
      String screen,
      String role,
      int age,
      String gender,
      String password) {
    Response r = client.createRaw(editor, login, screen, role, age, gender, password);
    try {
      long id = r.jsonPath().getLong("id");
      if (id > 0) ResourceTracker.registerPlayer(id);
    } catch (Exception ignored) {
    }
    return r;
  }

  /** Typed create (DTO). Auto-registers ID. */
  public PlayerCreateResponseDto create(
      String editor,
      String login,
      String screen,
      String role,
      int age,
      String gender,
      String password) {
    PlayerCreateResponseDto dto = client.create(editor, login, screen, role, age, gender, password);
    if (dto != null && dto.id() > 0) ResourceTracker.registerPlayer(dto.id());
    return dto;
  }

  /** Convenience: generate unique login/screen via RunIds. */
  public PlayerCreateResponseDto create(Editor editor, Role role, int age) {
    String login = RunIds.login(role.value());
    String screen = RunIds.screen(role.value());
    return create(editor.value(), login, screen, role.value(), age, Gender.MALE, Password.VALID);
  }

  // ---------- READ ----------

  public PlayerGetByPlayerIdResponseDto getById(long id) {
    return client.getById(id);
  }

  public PlayerGetAllResponseDto getAll() {
    return client.getAll();
  }

  // ---------- UPDATE ----------

  public PlayerUpdateResponseDto update(String editor, long id, PlayerUpdateRequestDto dto) {
    return client.update(editor, id, dto);
  }

  public PlayerUpdateResponseDto update(Editor editor, long id, PlayerUpdateRequestDto dto) {
    return update(editor.value(), id, dto);
  }

  // ---------- DELETE ----------

  public void delete(String editor, long id) {
    client.delete(editor, id);
  }

  public void delete(Editor editor, long id) {
    delete(editor.value(), id);
  }
}
