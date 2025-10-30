package com.ravejoy.player.listeners;

import com.ravejoy.player.http.ApiClient;
import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.testsupport.PlayerCleanup;
import com.ravejoy.player.testsupport.ResourceTracker;
import com.ravejoy.player.testsupport.RunIds;
import java.util.List;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public final class CleanupListener implements ISuiteListener {

  private static final String CLEANUP_EDITOR = "supervisor";

  @Override
  public void onStart(ISuite suite) {
    System.out.printf("[CLEANUP][onStart] suite=%s prefix=%s%n", suite.getName(), RunIds.prefix());
  }

  @Override
  public void onFinish(ISuite suite) {
    System.out.printf(
        "[CLEANUP][onFinish] suite=%s sweepPrefix=%s%n", suite.getName(), RunIds.prefix());

    var cleaner = new PlayerCleanup(new PlayerClient(new ApiClient()));

    List<Long> leftover = ResourceTracker.drainAllPlayers();
    if (leftover.isEmpty()) {
      System.out.println("[CLEANUP][onFinish] no explicit leftovers in tracker");
    } else {
      System.out.printf("[CLEANUP][onFinish] leftover ids=%s%n", leftover);
      cleaner.deleteByIds(CLEANUP_EDITOR, leftover);
    }

    cleaner.sweepByPrefix(CLEANUP_EDITOR, RunIds.prefix());
  }
}
