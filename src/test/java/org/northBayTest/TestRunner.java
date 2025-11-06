package org.northBayTest;

import courgette.api.CourgetteOptions;
import courgette.api.CourgetteRunLevel;
import courgette.api.CourgetteTestOutput;
import courgette.api.CucumberOptions;
import courgette.api.junit.Courgette;
import org.junit.runner.RunWith;

@RunWith(Courgette.class)
@CourgetteOptions(
    threads = 1,
    runLevel = CourgetteRunLevel.SCENARIO,
    rerunFailedScenarios = true,
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

}
