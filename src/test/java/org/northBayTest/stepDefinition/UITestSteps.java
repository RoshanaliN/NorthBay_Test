package org.northBayTest.stepDefinition;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.northBayTest.Hooks;
import org.northBayTest.modelClass.TestData;
import org.northBayTest.pages.BasePage;
import org.northBayTest.pages.ChatPage;
import org.northBayTest.pages.LoginPage;
import org.northBayTest.utils.MiniLLMHelper;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.northBayTest.utils.HtmlReportGenerator.appendScenarioResult;

public class UITestSteps {

  private static final Logger logger = LoggerFactory.getLogger(UITestSteps.class);

  BasePage basePage = new BasePage();
  LoginPage loginPage = new LoginPage();
  ChatPage chatPage = new ChatPage();
  String request = "";

  @Given("user navigates to {string}")
  public void userNavigatesToUrl(String url) {
    logger.info("Navigating to URL: {}", url);
    Hooks.getDriver().get(url);
  }

  @Then("user verify title of page is {string}")
  public void userVerifyTitleOfPageIs(String title) {
    String actualTitle = Hooks.getDriver().getTitle();
    logger.info("Verifying page title. Expected: '{}', Actual: '{}'", title, actualTitle);
    Assert.assertEquals(actualTitle, title);
  }

  @When("user click on button with text {string}")
  public void userClickOnButtonWithText(String buttonName) {
    logger.info("Clicking on button with text: {}", buttonName);
    basePage.clickButtonWithText(buttonName);
  }


  @When("user login with {string} and {string}")
  public void userLoginWithAnd(String username, String password) {
    logger.info("Logging in with username: {}", username);
    loginPage.enterDetailsAndLogin(username, password);
  }

  @Then("user validates welcome text on chat screen")
  public void userValidatesWelcomeTextOnChatScreen() {
    String expectedText = "Hi, Farrukh. Ready to dive in?";
    String actualText = chatPage.welcomeText();
    logger.info("Validating welcome text. Expected: '{}', Actual: '{}'", expectedText, actualText);
    Assert.assertEquals(expectedText, actualText);
  }

  @When("user enter {string} text in chat and sent to LLM")
  public void userEnterTextInChat(String text) {
    try {
      request = text;
      chatPage.enterTextInChat(text);
      chatPage.sendChatToLLM();
    } catch (Exception exception) {
      logger.error("Error sending text to LLM", exception);
      throw exception;
    }
  }

  @Then("user evaluates ask different question response")
  public void userEvaluatesAskDifferentResponse() {
    String expectedResult = "I'm sorry. I'm not able to assist with your request. Please ask a different question.";
    String actualResult = chatPage.getResponse();
    logger.info("Evaluating LLM response for 'ask different question'.");
    getResponseAndEvaluate(expectedResult, actualResult);
  }

  @Then("user validates text input box is cleared")
  public void userValidatesTextInputBoxIsCleared() {
    logger.info("Validating text input box is cleared.");
    Assert.assertTrue(chatPage.getInputText().isEmpty());
  }

  @When("user enter request and validate response from test-data in LLM")
  public void userRequestFromTestDataInLLM() {
    logger.info("Processing test-data list for LLM evaluation. Total entries: {}",
        Hooks.dataList.size());
    for (TestData data : Hooks.dataList) {
      userEnterTextInChat(data.getRequest());
      logger.info("Sending request from test-data: {}", data.getRequest());
      getResponseAndEvaluate(data.getResponse(), chatPage.getResponse());
    }
  }

  private void getResponseAndEvaluate(String expectedResponse, String actualResponse) {
    try {
      boolean formatCorrect = actualResponse != null && actualResponse.matches("^[A-Z].*\\.$");
      double score = MiniLLMHelper.getSimilarity(expectedResponse, actualResponse);
      String feedback = score >= 0.85
          ? "Pass - Response is semantically aligned."
          : "Fail - Response deviates in meaning or detail.";
      appendScenarioResult(request, expectedResponse, actualResponse,
          score, formatCorrect, feedback);
    } catch (Exception exception) {
      logger.error("Response evaluation error", exception);
    }
  }

  @Then("user validate scrolling")
  public void userValidateScrolling() {
    chatPage.scrollPage(chatPage.Xpath_responseFirstCopyButton);
    boolean displayed = Hooks.getDriver()
        .findElement(By.xpath(chatPage.Xpath_responseFirstCopyButton))
        .isDisplayed();
    logger.info("Scroll validation result: {}", displayed);
    Assert.assertTrue(displayed);
  }

  @When("user switch to Arabic")
  public void userSwitchToArabic() {
    logger.info("Switching chat language to Arabic.");
    chatPage.switchLanguage();
  }

  @Then("user validates arabic welcome text on chat screen")
  public void userValidatesArabicWelcomeTextOnChatScreen() {
    logger.info("Validating Arabic welcome text. Expected: '{}', Actual: '{}'", "Farrukh",
        chatPage.welcomeText());
    Assert.assertTrue(chatPage.welcomeText().contains("Farrukh"));
  }

}