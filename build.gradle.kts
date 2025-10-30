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
  implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

  testImplementation("org.testng:testng:7.11.0")
  testImplementation("io.qameta.allure:allure-testng:2.29.0")
  testImplementation("ch.qos.logback:logback-classic:1.4.14")
  testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
  testImplementation("org.mockito:mockito-core:5.12.0")
  testImplementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
  testImplementation(testFixtures(project(":")))

  testFixturesImplementation("io.rest-assured:rest-assured:5.5.0")
  testFixturesImplementation("org.testng:testng:7.11.0")
  testFixturesImplementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
}

tasks.test {
  useTestNG {
    // allow override: .\gradlew "-Dsuite=src/test/resources/testng-known-issues.xml" test
    val suiteProp = System.getProperty("suite") ?: "src/test/resources/testng.xml"
    suites(suiteProp)

    fun firstNonBlank(vararg keys: String): String? =
      keys.asSequence()
        .mapNotNull { System.getProperty(it) }
        .map { it.trim() }
        .firstOrNull { it.isNotEmpty() }

    // run: -Dgroups=functional
    firstNonBlank("testng.groups", "groups")
      ?.split(',')
      ?.map { it.trim() }
      ?.filter { it.isNotEmpty() }
      ?.toTypedArray()
      ?.let { includeGroups(*it) }

    // run: -DexcludeGroups=known-issues
    firstNonBlank("testng.excludeGroups", "excludeGroups")
      ?.split(',')
      ?.map { it.trim() }
      ?.filter { it.isNotEmpty() }
      ?.toTypedArray()
      ?.let { excludeGroups(*it) }
  }

  testLogging {
    events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    showStandardStreams = true
    exceptionFormat = TestExceptionFormat.FULL
  }

  systemProperty("allure.results.directory", "${project.buildDir}/allure-results")
}

// Dedicated task to run only known issues group (ignores suite files)
tasks.register<Test>("knownIssues") {
  description = "Run only tests marked as 'known-issues' group"
  group = "verification"

  useTestNG {
    includeGroups("known-issues")
  }

  testLogging {
    events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
    showStandardStreams = true
    exceptionFormat = TestExceptionFormat.FULL
  }
}

tasks.register("allureServe") {
    dependsOn("test")
    doLast {
        exec {
            commandLine("allure", "serve", "build/allure-results")
        }
    }
}

tasks.register("allureReport") {
    dependsOn("test")
    doLast {
        exec {
            commandLine("allure", "generate", "build/allure-results", "-o", "build/allure-report", "--clean")
        }
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
