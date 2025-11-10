package org.northBayTest;

import courgette.api.CourgetteAfterAll;
import courgette.api.CourgetteBeforeAll;
import courgette.api.CourgetteOptions;
import courgette.api.CourgetteRunLevel;
import courgette.api.CourgetteTestOutput;
import courgette.api.CucumberOptions;
import courgette.api.junit.Courgette;
import org.junit.runner.RunWith;

import static org.northBayTest.utils.HtmlReportGenerator.finalizeReport;
import static org.northBayTest.utils.HtmlReportGenerator.initializeReport;

@RunWith(Courgette.class)
@CourgetteOptions(
    threads = 1,
    runLevel = CourgetteRunLevel.SCENARIO,
    rerunFailedScenarios = false,
    rerunAttempts = 1,
    testOutput = CourgetteTestOutput.CONSOLE,
    reportTitle = "North Bay GovGpt Test Report",
    reportTargetDir = "build",
    environmentInfo = "browser=chrome; git_branch=master",
    cucumberOptions = @CucumberOptions(
        features = "src/test/resources/features",
        glue = {"org.northBayTest.stepDefinition","org.northBayTest"},
        tags = "@Regression and not @bug",
        plugin = {
            "pretty",
            "html:build/cucumber-report/cucumber.html"}
    ))


public class TestRunner {

  @CourgetteBeforeAll
  public static void responeReportSetUp() {
    initializeReport();
  }

  @CourgetteAfterAll
  public static void responeReportTearDown() {
    finalizeReport();
  }

}
