package com.ravejoy.player.listeners;

import com.ravejoy.player.config.AppConfig;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

/**
 * Sets suite-level parallel=methods and threadCount from AppConfig. Always enforces
 * AppConfig.threads() to keep configuration consistent.
 */
public class ParallelOverrideListener implements IAlterSuiteListener {
  private static final Logger log = LoggerFactory.getLogger(ParallelOverrideListener.class);

  @Override
  public void alter(List<XmlSuite> suites) {
    for (XmlSuite suite : suites) {
      int threads = AppConfig.threads();
      suite.setParallel(XmlSuite.ParallelMode.METHODS);
      suite.setThreadCount(threads);
      log.info(
          "[config] Suite='{}' → parallel=methods, threadCount={} (from AppConfig)",
          suite.getName(),
          threads);
    }
  }
}
