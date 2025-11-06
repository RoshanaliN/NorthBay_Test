package org.northBayTest.stepDefinition;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.junit.Assert;
import org.northBayTest.Hooks;
import org.northBayTest.modelClass.TestData;
import org.northBayTest.pages.BasePage;
import org.northBayTest.pages.ChatPage;
import org.northBayTest.pages.LoginPage;
import org.openqa.selenium.By;

import static org.northBayTest.utils.HtmlReportGenerator.appendScenarioResult;
import static org.northBayTest.utils.ResponseEvaluation.evaluateResponse;

public class UITestSteps {

  BasePage basePage = new BasePage();
  LoginPage loginPage = new LoginPage();
  ChatPage chatPage = new ChatPage();
  String request = "";

  @Given("user navigates to {string}")
  public void userNavigatesToUrl(String url) {
    Hooks.getDriver().get(url);
  }

  @Then("user verify title of page is {string}")
  public void userVerifyTitleOfPageIs(String title) {
    String actualTitle = Hooks.getDriver().getTitle();
    Assert.assertEquals(actualTitle, title);
  }

  @When("user click on button with text {string}")
  public void userClickOnButtonWithText(String buttonName) {
    basePage.clickButtonWithText(buttonName);
  }


  @When("user login with {string} and {string}")
  public void userLoginWithAnd(String username, String password) {
    loginPage.enterDetailsAndLogin(username, password);
  }

  @Then("user validates welcome text on chat screen")
  public void userValidatesWelcomeTextOnChatScreen() {
    Assert.assertEquals("Hi, Farrukh. Ready to dive in?", chatPage.welcomeText());
  }

  @When("user enter {string} text in chat and sent to LLM")
  public void userEnterTextInChat(String text) {
    try {
      request = text;
      chatPage.enterTextInChat(text);
      chatPage.sendChatToLLM();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Then("user evaluates ask different question response")
  public void userEvaluatesAskDifferentResponse() {
    String expectedResult = "I'm sorry. I’m not able to assist with your request. Please ask a different question.";
    getResponseAndEvaluate(expectedResult, chatPage.getResponse());
  }

  @Then("user validates text input box is cleared")
  public void userValidatesTextInputBoxIsCleared() {
    Assert.assertTrue(chatPage.getInputText().isEmpty());
  }

  @When("user enter request and validate response from test-data in LLM")
  public void userRequestFromTestDataInLLM() {
    for (TestData data : Hooks.dataList) {
      userEnterTextInChat(data.getRequest());
      getResponseAndEvaluate(data.getResponse(), chatPage.getResponse());
    }
  }

  private void getResponseAndEvaluate(String expectedResponse, String actualResponse) {
    Map<String, Object> result = evaluateResponse(expectedResponse, actualResponse);
    appendScenarioResult(request, expectedResponse, actualResponse,
        (double) result.get("score"), (boolean) result.get("format_correct"),
        (String) result.get("feedback"));
  }

  @Then("user validate scrolling")
  public void userValidateScrolling(){
    chatPage.scrollPage(chatPage.Xpath_responseFirstCopyButton);
    Assert.assertTrue(
        Hooks.getDriver().findElement(By.xpath(chatPage.Xpath_responseFirstCopyButton))
            .isDisplayed());
  }

  @When("user switch to Arabic")
  public void userSwitchToArabic(){
    chatPage.switchLanguage();
  }

  @Then("user validates arabic welcome text on chat screen")
  public void userValidatesArabicWelcomeTextOnChatScreen() {
    Assert.assertEquals("هلا, Farrukh. هل انت مستعد للبدء؟", chatPage.welcomeText());
  }

}