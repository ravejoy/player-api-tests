package com.ravejoy.player.steps;

import com.ravejoy.player.data.model.PlayerCreateData;
import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.players.dto.*;
import com.ravejoy.player.testsupport.*;
import io.restassured.response.Response;

public final class PlayerSteps {

  private final StepsSupport support;
  private final PlayerClient client;

  public PlayerSteps() {
    this(new StepsSupport());
  }

  public PlayerSteps(StepsSupport support) {
    this.support = support;
    this.client = support.players();
  }

  public Response createRaw(
      String editor,
      String login,
      String screen,
      String role,
      int age,
      String gender,
      String password) {
    Response r = client.createRaw(editor, login, screen, role, age, gender, password);
    support.registerIdFrom(r);
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

  public Response createAs(Editor editor, PlayerCreateData d) {
    Response r =
        client.createRaw(
            editor.value(), d.login(), d.screenName(), d.role(), d.age(), d.gender(), d.password());
    support.registerIdFrom(r);
    return r;
  }

  public PlayerCreateResponseDto create(Editor editor, PlayerCreateData d) {
    PlayerCreateResponseDto dto =
        client.create(
            editor.value(), d.login(), d.screenName(), d.role(), d.age(), d.gender(), d.password());
    if (dto != null && dto.id() > 0) ResourceTracker.registerPlayer(dto.id());
    return dto;
  }

  public PlayerGetByPlayerIdResponseDto getById(long id) {
    return client.getById(id);
  }

  /*public PlayerGetByPlayerIdResponseDto getById(long id) {
    var resp = client.getByIdRaw(id);
    return Jsons.toDtoOrNull(resp, PlayerGetByPlayerIdResponseDto.class);
  }*/

  public PlayerGetAllResponseDto getAll() {
    return client.getAll();
  }

  public PlayerUpdateResponseDto update(String editor, long id, PlayerUpdateRequestDto dto) {
    return client.update(editor, id, dto);
  }

  public PlayerUpdateResponseDto update(Editor editor, long id, PlayerUpdateRequestDto dto) {
    return update(editor.value(), id, dto);
  }

  public void delete(String editor, long id) {
    client.delete(editor, id);
  }

  public void delete(Editor editor, long id) {
    delete(editor.value(), id);
  }

  public Response getByIdRaw(long id) {
    return client.getByIdRaw(id);
  }

  public Response deleteAndReturn(Editor editor, long id) {
    return client.deleteRaw(editor.value(), id);
  }
}
