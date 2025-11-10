package org.northBayTest.pages;

import java.util.List;
import org.northBayTest.Hooks;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ChatPage extends BasePage {

  private WebDriver driver;

  @FindBy(css = ".welcome-text h1")
  private WebElement textWelcome;

  @FindBy(id = "chat-input")
  private WebElement textBoxChatInput;

  @FindBy(id = "send-message-button")
  private WebElement buttonSendMessage;

  @FindBy(id = "response-content-container")
  private List<WebElement> textResponse;

  @FindBy(xpath = "//div[contains(@class,'chat-{message.role}')]")
  private List<WebElement> textRequest;

  @FindBy(xpath = "//button[contains(@class,'menu-hover')]")
  private WebElement buttonBottomMenu;

  @FindBy(xpath = "//div[string() = 'Switch to Arabic']/parent::div//div[contains(@class,'toggle')]")
  private WebElement toggleSwitchToArabic;

  @FindBy(xpath = "//p[@data-placeholder='أرسل رسالة']")
  private WebElement textBoxChatInputArabic;

  @FindBy(xpath = "//html[@lang='ar']")
  private WebElement arabicWebpage;

  public final String Xpath_workingOnIt = "//*[text()='Working on it..']";
  public final String Xpath_classifyingYourQuery = "//*[text()='Classifying your query']";
  public final String Xpath_responseCopyButton = "//div[div[contains(@class,'chat-assistant')]]//button[(@aria-label='Copy' or @aria-label='نسخ')][last()]";
  public final String Xpath_responseFirstCopyButton = "//div[div[contains(@class,'chat-assistant')]]//button[@aria-label='Copy']";


  public ChatPage() {
    this.driver = Hooks.getDriver();
    PageFactory.initElements(driver, this);
  }

  public String welcomeText() {
    return textWelcome.getText();
  }

  public void enterTextInChat(String text) {
    textBoxChatInput.sendKeys(text);
  }

  public void sendChatToLLM() {
    buttonSendMessage.click();
  }

  public String getResponse() {
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(Xpath_workingOnIt)));
    wait.until(
        ExpectedConditions.invisibilityOfElementLocated(By.xpath(Xpath_classifyingYourQuery)));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(Xpath_responseCopyButton)));
    return textResponse.get(textResponse.size() - 1).getText().replace("\n", "");
  }

  public String getInputText() {
    return textBoxChatInput.getText();
  }

  public void switchLanguage() {
    buttonBottomMenu.click();
    toggleSwitchToArabic.click();
    buttonBottomMenu.click();
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//html[@lang='ar']")));
    arabicWebpage.isDisplayed();
    textBoxChatInputArabic.isDisplayed();
  }
}
