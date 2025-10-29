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
      deleteWithRetry(editor, id);
    }
  }

  public void sweepByPrefix(String editor, String prefix) {
    int passes = 0;
    int deletedThisPass;
    do {
      passes++;
      deletedThisPass = 0;

      PlayerGetAllResponseDto all;
      try {
        all = client.getAll();
      } catch (Exception e) {
        return;
      }

      Predicate<PlayerItem> match =
          i -> i.screenName() != null && i.screenName().startsWith(prefix);

      for (PlayerItem i : all.players()) {
        if (match.test(i)) {
          if (deleteWithRetry(editor, i.id())) {
            deletedThisPass++;
          }
        }
      }

      if (deletedThisPass > 0) {
        try {
          Thread.sleep(150L);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    } while (deletedThisPass > 0 && passes < 5);
  }

  private boolean deleteWithRetry(String editor, long id) {
    int attempts = 0;
    while (attempts < 3) {
      attempts++;
      try {
        client.delete(editor, id);
        return true;
      } catch (Exception ignored) {
      }

      try {
        Thread.sleep(100L * attempts);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        break;
      }
    }
    return false;
  }
}
