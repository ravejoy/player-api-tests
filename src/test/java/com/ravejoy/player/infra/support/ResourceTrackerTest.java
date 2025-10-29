package com.ravejoy.player.infra.support;

import com.ravejoy.player.testsupport.ResourceTracker;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@Test(groups = {"infra"})
public class ResourceTrackerTest {

  @AfterMethod
  public void clearTL() {
    ResourceTracker.clear();
  }

  public void drainReturnsAndClearsCurrentThreadOnly() {
    ResourceTracker.registerPlayer(1L);
    ResourceTracker.registerPlayer(2L);
    ResourceTracker.registerPlayer(3L);

    List<Long> drained = ResourceTracker.drainPlayers();
    Assert.assertEquals(drained.size(), 3);
    Assert.assertTrue(drained.containsAll(List.of(1L, 2L, 3L)));

    List<Long> drainedAgain = ResourceTracker.drainPlayers();
    Assert.assertTrue(drainedAgain.isEmpty(), "ThreadLocal must be cleared after drain");
  }

  public void threadIsolation() throws Exception {
    ResourceTracker.registerPlayer(100L);

    var es = Executors.newSingleThreadExecutor();
    Callable<List<Long>> task =
        () -> {
          ResourceTracker.registerPlayer(200L);
          return ResourceTracker.drainPlayers();
        };
    Future<List<Long>> fut = es.submit(task);
    List<Long> otherThreadDrained = fut.get();
    es.shutdown();

    Assert.assertEquals(otherThreadDrained, List.of(200L));

    List<Long> mainThreadDrained = ResourceTracker.drainPlayers();
    Assert.assertEquals(mainThreadDrained, List.of(100L));
  }

  public void clearEmptiesCurrentThread() {
    ResourceTracker.registerPlayer(10L);
    ResourceTracker.registerPlayer(11L);
    ResourceTracker.clear();
    List<Long> drained = ResourceTracker.drainPlayers();
    Assert.assertTrue(drained.isEmpty());
  }
}
