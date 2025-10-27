package com.ravejoy.player.listeners;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.*;

public class LoggingTestListener implements ITestListener {
  private static final Logger log = LoggerFactory.getLogger(LoggingTestListener.class);

  @Override
  public void onStart(ITestContext ctx) {
    var suite = ctx.getSuite().getXmlSuite();
    var xt = ctx.getCurrentXmlTest();
    var mode = xt != null ? xt.getParallel() : suite.getParallel();
    var tc = xt != null ? xt.getThreadCount() : suite.getThreadCount();
    log.info(
        "===> Suite start: {} | test={} | url={} | threads={} | parallel={}",
        ctx.getSuite().getName(),
        xt != null ? xt.getName() : "(n/a)",
        com.ravejoy.player.config.AppConfig.apiUrl(),
        com.ravejoy.player.config.AppConfig.threads(),
        mode + "(" + tc + ")");
  }

  @Override
  public void onFinish(ITestContext ctx) {
    log.info(
        "<=== Suite finish: {} | passed={}, failed={}, skipped={}",
        ctx.getName(),
        ctx.getPassedTests().size(),
        ctx.getFailedTests().size(),
        ctx.getSkippedTests().size());
  }

  @Override
  public void onTestStart(ITestResult r) {
    String id = id(r);
    MDC.put("testName", id);
    log.info("START {} params={}", id, Arrays.toString(r.getParameters()));
  }

  @Override
  public void onTestSuccess(ITestResult r) {
    log.info("PASS  {} in {} ms", id(r), durationMs(r));
    MDC.remove("testName");
  }

  @Override
  public void onTestFailure(ITestResult r) {
    log.error("FAIL  {} in {} ms - {}", id(r), durationMs(r), r.getThrowable());
    MDC.remove("testName");
  }

  @Override
  public void onTestSkipped(ITestResult r) {
    log.warn("SKIP  {}", id(r));
    MDC.remove("testName");
  }

  @Override
  public void onTestFailedWithTimeout(ITestResult r) {
    onTestFailure(r);
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult r) {
    MDC.remove("testName");
  }

  private static String id(ITestResult r) {
    return r.getTestClass().getName() + "#" + r.getMethod().getMethodName();
  }

  private static long durationMs(ITestResult r) {
    return r.getEndMillis() - r.getStartMillis();
  }
}
