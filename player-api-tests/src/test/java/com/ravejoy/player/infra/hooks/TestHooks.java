package com.ravejoy.player.infra.hooks;

import com.ravejoy.player.http.ApiClient;
import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.testsupport.PlayerCleanup;
import com.ravejoy.player.testsupport.ResourceTracker;
import com.ravejoy.player.testsupport.RunIds;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;

public class TestHooks {

  private static final String CLEANUP_EDITOR = "supervisor";

  @AfterMethod(alwaysRun = true)
  public void cleanupPerTest() {
    var api = new ApiClient();
    var client = new PlayerClient(api);
    var cleaner = new PlayerCleanup(client);
    cleaner.deleteByIds(CLEANUP_EDITOR, ResourceTracker.drainPlayers());
  }

  @AfterSuite(alwaysRun = true)
  public void sweepLeftovers() {
    var api = new ApiClient();
    var client = new PlayerClient(api);
    var cleaner = new PlayerCleanup(client);
    cleaner.sweepByPrefix(CLEANUP_EDITOR, RunIds.prefix());
  }
}
