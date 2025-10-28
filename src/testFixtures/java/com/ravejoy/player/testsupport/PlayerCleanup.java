package com.ravejoy.player.testsupport;

import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.players.dto.PlayerGetAllResponseDto;
import com.ravejoy.player.players.dto.PlayerItem;
import java.util.List;
import java.util.function.Predicate;

public final class PlayerCleanup {

  private final PlayerClient client;

  public PlayerCleanup(PlayerClient client) {
    this.client = client;
  }

  public void deleteByIds(String editor, List<Long> ids) {
    for (Long id : ids) {
      try {
        client.delete(editor, id);
      } catch (Exception ignored) {
      }
    }
  }

  public void sweepByPrefix(String editor, String prefix) {
    try {
      PlayerGetAllResponseDto all = client.getAll();
      Predicate<PlayerItem> p = i -> i.screenName() != null && i.screenName().startsWith(prefix);
      all.players().stream()
          .filter(p)
          .forEach(
              i -> {
                try {
                  client.delete(editor, i.id());
                } catch (Exception ignored) {
                }
              });
    } catch (Exception ignored) {
    }
  }
}
