package com.ravejoy.player.steps;

import com.ravejoy.player.http.ApiClient;
import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.players.dto.*;
import com.ravejoy.player.testsupport.*;
import io.restassured.response.Response;

public final class PlayerSteps {

  private final PlayerClient client = new PlayerClient(new ApiClient());

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

  public PlayerCreateResponseDto create(Editor editor, Role role, int age) {
    String login = RunIds.login(role.value());
    String screen = RunIds.screen(role.value());
    return create(editor.value(), login, screen, role.value(), age, Gender.MALE, Password.VALID);
  }

  // -------- SHORTCUTS (no-args for login/screen) --------

  public Response createAs(Editor editor, Role role) {
    return createRaw(
        editor.value(),
        RunIds.login(role.value()),
        RunIds.screen(role.value()),
        role.value(),
        24,
        Gender.MALE,
        Password.VALID);
  }

  public Response createAsSupervisor(Role role) {
    return createAs(Editor.SUPERVISOR, role);
  }

  public Response createAsAdmin(Role role) {
    return createAs(Editor.ADMIN, role);
  }

  public Response createAsUser(Role role) {
    return createAs(Editor.USER, role);
  }

  // -------- SHORTCUTS (explicit login/screen) --------

  public Response createAs(Editor editor, String login, String screen, Role role) {
    return createRaw(editor.value(), login, screen, role.value(), 24, Gender.MALE, Password.VALID);
  }

  public Response createAsSupervisor(String login, String screen, Role role) {
    return createAs(Editor.SUPERVISOR, login, screen, role);
  }

  public Response createAsAdmin(String login, String screen, Role role) {
    return createAs(Editor.ADMIN, login, screen, role);
  }

  public Response createAsUser(String login, String screen, Role role) {
    return createAs(Editor.USER, login, screen, role);
  }

  // -------- SHORTCUTS (targetRole as String for RBAC matrices) --------

  public Response createAs(Editor editor, String targetRole) {
    return createRaw(
        editor.value(),
        RunIds.login(targetRole),
        RunIds.screen(targetRole),
        targetRole,
        24,
        Gender.MALE,
        Password.VALID);
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
