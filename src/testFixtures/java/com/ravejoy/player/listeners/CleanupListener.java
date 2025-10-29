package com.ravejoy.player.listeners;

import com.ravejoy.player.http.ApiClient;
import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.testsupport.PlayerCleanup;
import com.ravejoy.player.testsupport.ResourceTracker;
import com.ravejoy.player.testsupport.RunIds;
import org.testng.*;

public final class CleanupListener implements IInvokedMethodListener, ISuiteListener {

  private static final String CLEANUP_EDITOR = "supervisor";

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult result) {
    if (!method.isTestMethod()) return;
    var cleaner = new PlayerCleanup(new PlayerClient(new ApiClient()));
    cleaner.deleteByIds(CLEANUP_EDITOR, ResourceTracker.drainPlayers());
  }

  @Override
  public void onFinish(ISuite suite) {
    var cleaner = new PlayerCleanup(new PlayerClient(new ApiClient()));
    cleaner.sweepByPrefix(CLEANUP_EDITOR, RunIds.prefix());
  }
}
