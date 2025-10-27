import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  java
  jacoco
  id("com.diffplug.spotless") version "6.25.0"
  id("java-test-fixtures")
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("io.rest-assured:rest-assured:5.5.0")
  implementation("org.apache.httpcomponents:httpclient:4.5.14")
  implementation("ch.qos.logback:logback-classic:1.4.14")

  testImplementation("org.testng:testng:7.11.0")
  testImplementation("io.qameta.allure:allure-testng:2.29.0")
  testImplementation("ch.qos.logback:logback-classic:1.4.14")
  testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
  testImplementation("org.mockito:mockito-core:5.12.0")
  testImplementation(testFixtures(project(":")))

  testFixturesImplementation("io.rest-assured:rest-assured:5.5.0")
  testFixturesImplementation("org.testng:testng:7.11.0")
}

tasks.test {
  useTestNG {
    suites("src/test/resources/testng.xml")

    System.getProperty("groups")?.takeIf { it.isNotBlank() }?.let {
      includeGroups(*it.split(',').map(String::trim).toTypedArray())
    }
    System.getProperty("excludeGroups")?.takeIf { it.isNotBlank() }?.let {
      excludeGroups(*it.split(',').map(String::trim).toTypedArray())
    }
  }

  testLogging {
    events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    showStandardStreams = true
    exceptionFormat = TestExceptionFormat.FULL
  }
}

jacoco {
  toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required.set(true)
    html.required.set(true)
    csv.required.set(false)
  }
}

spotless {
  java {
    target("src/**/*.java")
    googleJavaFormat("1.22.0")
    trimTrailingWhitespace()
    endWithNewline()
  }
}
