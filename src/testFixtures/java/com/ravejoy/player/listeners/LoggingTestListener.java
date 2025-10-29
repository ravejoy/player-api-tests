package com.ravejoy.player.listeners;

import com.ravejoy.player.testsupport.RunIds;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public final class LoggingTestListener implements ITestListener {
  private static final Logger log = LoggerFactory.getLogger(LoggingTestListener.class);
  private static final String START_NANOS = "startNanos";

  @Override
  public void onTestStart(ITestResult tr) {
    String testName = tr.getTestClass().getName() + "#" + tr.getMethod().getMethodName();
    MDC.put("test", testName);
    MDC.put("thread", Thread.currentThread().getName());
    MDC.put("runId", RunIds.prefix());
    tr.setAttribute(START_NANOS, System.nanoTime());
    log.info("START {} params={}", testName, Arrays.toString(tr.getParameters()));
  }

  @Override
  public void onTestSuccess(ITestResult tr) {
    String testName = tr.getTestClass().getName() + "#" + tr.getMethod().getMethodName();
    Long t = (Long) tr.getAttribute(START_NANOS);
    long ms = t == null ? 0L : (System.nanoTime() - t) / 1_000_000L;
    log.info("PASS {} durationMs={}", testName, ms);
    MDC.clear();
  }

  @Override
  public void onTestFailure(ITestResult tr) {
    String testName = tr.getTestClass().getName() + "#" + tr.getMethod().getMethodName();
    Long t = (Long) tr.getAttribute(START_NANOS);
    long ms = t == null ? 0L : (System.nanoTime() - t) / 1_000_000L;
    log.error("FAIL {} durationMs={}", testName, ms);
    MDC.clear();
  }

  @Override
  public void onTestSkipped(ITestResult tr) {
    String testName = tr.getTestClass().getName() + "#" + tr.getMethod().getMethodName();
    log.warn("SKIP {}", testName);
    MDC.clear();
  }

  @Override
  public void onStart(ITestContext context) {}

  @Override
  public void onFinish(ITestContext context) {}
}
