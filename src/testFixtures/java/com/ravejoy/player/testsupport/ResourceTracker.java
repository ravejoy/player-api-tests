package com.ravejoy.player.testsupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class ResourceTracker {

  private static final ThreadLocal<Queue<Long>> TL_QUEUE =
      ThreadLocal.withInitial(ConcurrentLinkedQueue::new);

  private ResourceTracker() {}

  public static void registerPlayer(long id) {
    if (id > 0L) {
      TL_QUEUE.get().add(id);
    }
  }

  public static List<Long> drainPlayers() {
    Queue<Long> q = TL_QUEUE.get();
    var drained = new ArrayList<Long>(q.size());
    Long id;
    while ((id = q.poll()) != null) {
      drained.add(id);
    }
    return drained;
  }

  public static void clear() {
    TL_QUEUE.get().clear();
  }
}
