package com.ravejoy.player.players;

import com.ravejoy.player.http.ApiClient;
import com.ravejoy.player.players.dto.PlayerCreateResponseDto;
import com.ravejoy.player.players.dto.PlayerDeleteRequestDto;
import com.ravejoy.player.players.dto.PlayerGetAllResponseDto;
import com.ravejoy.player.players.dto.PlayerGetByPlayerIdRequestDto;
import com.ravejoy.player.players.dto.PlayerGetByPlayerIdResponseDto;
import com.ravejoy.player.players.dto.PlayerUpdateRequestDto;
import com.ravejoy.player.players.dto.PlayerUpdateResponseDto;
import io.restassured.response.Response;
import java.util.Map;

public final class PlayerClient {

  private final ApiClient api;

  public PlayerClient(ApiClient api) {
    this.api = api;
  }

  public PlayerCreateResponseDto create(
      String editor,
      String login,
      String screenName,
      String role,
      int age,
      String gender,
      String password) {
    Map<String, Object> q =
        Map.of(
            "login", login,
            "screenName", screenName,
            "role", role,
            "age", age,
            "gender", gender,
            "password", password);
    return api.get(PlayerEndpoints.CREATE + editor, q).as(PlayerCreateResponseDto.class);
  }

  public Response createRaw(
      String editor,
      String login,
      String screenName,
      String role,
      int age,
      String gender,
      String password) {
    Map<String, Object> q =
        Map.of(
            "login", login,
            "screenName", screenName,
            "role", role,
            "age", age,
            "gender", gender,
            "password", password);
    return api.get(PlayerEndpoints.CREATE + editor, q);
  }

  public void delete(String editor, long playerId) {
    api.deleteWithBody(PlayerEndpoints.DELETE + editor, new PlayerDeleteRequestDto(playerId));
  }

  public PlayerGetByPlayerIdResponseDto getById(long id) {
    return api.post(PlayerEndpoints.GET, new PlayerGetByPlayerIdRequestDto(id))
        .as(PlayerGetByPlayerIdResponseDto.class);
  }

  public PlayerGetAllResponseDto getAll() {
    return api.get(PlayerEndpoints.GET_ALL).as(PlayerGetAllResponseDto.class);
  }

  public PlayerUpdateResponseDto update(String editor, long id, PlayerUpdateRequestDto dto) {
    return api.patch(PlayerEndpoints.UPDATE + editor + "/" + id, dto)
        .as(PlayerUpdateResponseDto.class);
  }

  public Response getByIdRaw(long id) {
    return api.post(PlayerEndpoints.GET, new PlayerGetByPlayerIdRequestDto(id));
  }

  public Response deleteRaw(String editor, long playerId) {
  return api.deleteWithBody(PlayerEndpoints.DELETE + editor, new PlayerDeleteRequestDto(playerId));
}
}
