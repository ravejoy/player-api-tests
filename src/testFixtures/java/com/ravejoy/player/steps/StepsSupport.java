package com.ravejoy.player.steps;

import com.ravejoy.player.http.ApiClient;
import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.testsupport.ResourceTracker;
import io.restassured.response.Response;

public final class StepsSupport {
  private final ApiClient apiClient;
  private final PlayerClient playerClient;

  public StepsSupport() {
    this.apiClient = new ApiClient();
    this.playerClient = new PlayerClient(apiClient);
  }

  public StepsSupport(ApiClient apiClient) {
    this.apiClient = apiClient;
    this.playerClient = new PlayerClient(apiClient);
  }

  public ApiClient api() {
    return apiClient;
  }

  public PlayerClient players() {
    return playerClient;
  }

  public void registerIdFrom(Response resp) {
    try {
      long id = resp.jsonPath().getLong("id");
      if (id > 0) ResourceTracker.registerPlayer(id);
    } catch (Exception ignored) {
    }
  }
}
