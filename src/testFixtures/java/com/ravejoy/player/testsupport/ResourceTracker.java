package com.ravejoy.player.testsupport;

import java.util.ArrayList;
import java.util.List;

public final class ResourceTracker {
  private static final ThreadLocal<List<Long>> CREATED_PLAYER_IDS =
      ThreadLocal.withInitial(ArrayList::new);

  private ResourceTracker() {}

  public static void registerPlayer(long id) {
    CREATED_PLAYER_IDS.get().add(id);
  }

  public static List<Long> drainPlayers() {
    List<Long> copy = new ArrayList<>(CREATED_PLAYER_IDS.get());
    CREATED_PLAYER_IDS.get().clear();
    return copy;
  }

  public static void clear() {
    CREATED_PLAYER_IDS.get().clear();
  }
}
