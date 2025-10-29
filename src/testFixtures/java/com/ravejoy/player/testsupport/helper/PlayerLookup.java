package com.ravejoy.player.testsupport.helper;

import com.ravejoy.player.http.ApiClient;
import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.players.dto.PlayerGetAllResponseDto;
import com.ravejoy.player.players.dto.PlayerGetByPlayerIdResponseDto;
import com.ravejoy.player.players.dto.PlayerItem;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class PlayerLookup {

  private PlayerLookup() {}

  private static List<PlayerItem> items() {
    return Optional.ofNullable(getAll().players()).orElse(List.of());
  }

  public static int countByPrefix(String prefix) {
    return (int)
        items().stream()
            .map(PlayerItem::screenName)
            .filter(Objects::nonNull)
            .filter(s -> s.startsWith(prefix))
            .count();
  }

  public static int count(Predicate<PlayerItem> filter) {
    return (int) items().stream().filter(filter).count();
  }

  public static boolean existsByScreenName(String screenName) {
    return items().stream()
        .map(PlayerItem::screenName)
        .filter(Objects::nonNull)
        .anyMatch(screenName::equals);
  }

  public static PlayerGetByPlayerIdResponseDto getById(long id) {
    var client = new PlayerClient(new ApiClient());
    return client.getById(id);
  }

  public static PlayerGetAllResponseDto getAll() {
    var client = new PlayerClient(new ApiClient());
    return client.getAll();
  }
}
