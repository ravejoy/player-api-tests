package com.ravejoy.player.listeners;

import com.ravejoy.player.http.ApiClient;
import com.ravejoy.player.players.PlayerClient;
import com.ravejoy.player.testsupport.PlayerCleanup;
import com.ravejoy.player.testsupport.ResourceTracker;
import com.ravejoy.player.testsupport.RunIds;
import java.util.List;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestResult;

public final class CleanupListener implements IInvokedMethodListener, ISuiteListener {

  private static final String CLEANUP_EDITOR = "supervisor";

  @Override
  public void onStart(ISuite suite) {
    System.out.printf("[CLEANUP][onStart] suite=%s prefix=%s%n", suite.getName(), RunIds.prefix());
  }

  @Override
  public void afterInvocation(IInvokedMethod method, ITestResult result) {
    if (!method.isTestMethod()) return;

    List<Long> ids = ResourceTracker.drainPlayers();
    if (ids.isEmpty()) return;

    var cleaner = new PlayerCleanup(new PlayerClient(new ApiClient()));
    System.out.printf(
        "[CLEANUP][afterInvocation] %s#%s ids=%s%n",
        method.getTestMethod().getRealClass().getSimpleName(),
        method.getTestMethod().getMethodName(),
        ids);
    cleaner.deleteByIds(CLEANUP_EDITOR, ids);
  }

  @Override
  public void onFinish(ISuite suite) {
    System.out.printf("[CLEANUP][onFinish] suite=%s%n", suite.getName());

    List<Long> left = ResourceTracker.drainAllPlayers();
    if (left.isEmpty()) {
      System.out.println("[CLEANUP][onFinish] nothing to delete");
      return;
    }
    var cleaner = new PlayerCleanup(new PlayerClient(new ApiClient()));
    System.out.printf("[CLEANUP][onFinish] leftover ids=%s%n", left);
    cleaner.deleteByIds(CLEANUP_EDITOR, left);
  }
}
