package com.ravejoy.player.testsupport;

import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.players.dto.PlayerGetAllResponseDto;
import com.ravejoy.player.players.dto.PlayerItem;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PlayerCleanup {

  private static final int MAX_RETRIES = 3;
  private static final long RETRY_SLEEP_MS = 200L;

  private final PlayerClient client;

  public PlayerCleanup(PlayerClient client) {
    this.client = client;
  }

  public void deleteByIds(String editor, List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      System.out.println("[CLEANUP][deleteByIds] nothing to delete");
      return;
    }
    final List<Long> unique = new ArrayList<>(new LinkedHashSet<>(ids));
    System.out.printf("[CLEANUP][deleteByIds] editor=%s ids=%s%n", editor, unique);

    Set<Long> remaining = new LinkedHashSet<>(unique);
    int attempt = 1;

    while (!remaining.isEmpty() && attempt <= MAX_RETRIES) {
      for (Long id : new ArrayList<>(remaining)) {
        try {
          client.delete(editor, id);
          System.out.printf("[CLEANUP][DELETE] id=%d%n", id);
        } catch (Exception e) {
          System.out.printf("[CLEANUP][DELETE][ERROR] id=%d err=%s%n", id, e.toString());
        }
      }
      sleep(RETRY_SLEEP_MS);
      remaining = idsStillPresent(remaining);
      if (!remaining.isEmpty()) {
        System.out.printf("[CLEANUP][VERIFY] attempt=%d still=%s%n", attempt, remaining);
      }
      attempt++;
    }

    if (!remaining.isEmpty()) {
      System.out.printf("[CLEANUP][WARN] leftover=%s%n", remaining);
    } else {
      System.out.println("[CLEANUP][OK] no leftovers");
    }
  }

  public void sweepByPrefix(String editor, String prefix) {
    System.out.printf("[CLEANUP][sweep] editor=%s prefix=%s%n", editor, prefix);
    try {
      PlayerGetAllResponseDto all = client.getAll();
      if (all == null || all.players() == null || all.players().isEmpty()) {
        System.out.println("[CLEANUP][sweep] no players");
        return;
      }

      List<Long> toDelete = new ArrayList<>();
      for (PlayerItem p : all.players()) {
        String screen = p.screenName();
        if (screen != null && screen.startsWith(prefix)) {
          toDelete.add(p.id());
        }
      }

      System.out.printf("[CLEANUP][sweep] found=%s%n", toDelete);
      deleteByIds(editor, toDelete);

    } catch (Exception e) {
      System.out.printf("[CLEANUP][sweep][ERROR] err=%s%n", e.toString());
    }
  }

  private Set<Long> idsStillPresent(Set<Long> ids) {
    Set<Long> present = new LinkedHashSet<>();
    try {
      PlayerGetAllResponseDto all = client.getAll();
      if (all == null || all.players() == null) return present;
      for (PlayerItem p : all.players()) {
        if (ids.contains(p.id())) {
          present.add(p.id());
        }
      }
    } catch (Exception e) {
      System.out.printf("[CLEANUP][VERIFY][ERROR] err=%s%n", e.toString());
    }
    return present;
  }

  private static void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException ignored) {
    }
  }
}
