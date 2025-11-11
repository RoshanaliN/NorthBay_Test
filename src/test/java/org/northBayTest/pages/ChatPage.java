package org.northBayTest.pages;

import java.util.List;
import org.northBayTest.Hooks;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatPage extends BasePage {

  private static final Logger logger = LoggerFactory.getLogger(ChatPage.class);

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

  @FindBy(xpath = "//html[@lang='ar']")
  private WebElement arabicWebpage;

  public final String Xpath_workingOnIt = "//*[text()='Working on it..']";
  public final String Xpath_classifyingYourQuery = "//*[text()='Classifying your query']";
  public final String Xpath_finalizingTheAnswer = "//*[text()='Finalizing the answer']";
  public final String Xpath_responseCopyButton = "//div[div[contains(@class,'chat-assistant')]]//button[contains(@class,'copy')][last()]";
  public final String Xpath_responseFirstCopyButton = "//div[div[contains(@class,'chat-assistant')]]//button[contains(@class,'copy')]";


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
    wait.until(
        ExpectedConditions.invisibilityOfElementLocated(By.xpath(Xpath_finalizingTheAnswer)));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(Xpath_responseCopyButton)));
    return textResponse.get(textResponse.size() - 1).getText().replace("\n", "");
  }

  public String getInputText() {
    return textBoxChatInput.getText();
  }

  public void switchLanguage() {
    logger.info("Switching chat language to Arabic.");
    buttonBottomMenu.click();
    toggleSwitchToArabic.click();
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//html[@lang='ar']")));
    boolean arabicPageDisplayed = arabicWebpage.isDisplayed();
    try {
      int counter = 0;
      String fieldText;
      do {
        Thread.sleep(500);
        fieldText = driver.findElement(By.xpath("//div[contains(@class,'text-error-icon')]"))
            .getText();
        counter++;
      } while (fieldText.equals("تسجيل الخروج") && counter < 5);
    } catch (Exception exception) {
      logger.error("Error changing language", exception);
    }
    buttonBottomMenu.click();
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@role='menu']")));
    logger.info("Arabic webpage displayed: {}", arabicPageDisplayed);
  }
}
