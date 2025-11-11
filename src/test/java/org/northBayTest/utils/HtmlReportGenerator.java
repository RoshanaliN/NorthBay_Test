package org.northBayTest.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlReportGenerator {

  private static final Logger logger = LoggerFactory.getLogger(HtmlReportGenerator.class);

  private static final String REPORT_PATH = "llmEvaluationReport.html";
  private static final AtomicInteger scenarioCounter = new AtomicInteger(
      0);


  public static void initializeReport() {
    logger.info("Initializing LLM evaluation HTML report at: {}", REPORT_PATH);
    try (FileWriter writer = new FileWriter(REPORT_PATH, false)) {
      writer.write("<!DOCTYPE html>\n<html>\n<head>\n");
      writer.write("<title>Scenario Report</title>\n");
      writer.write("<style>\n");
      writer.write("body { font-family: Arial, sans-serif; background-color: #f9f9f9; }\n");
      writer.write("table { border-collapse: collapse; width: 80%; margin: 20px auto; }\n");
      writer.write("th, td { border: 1px solid #333; padding: 8px; text-align: center; }\n");
      writer.write("th { background-color: #4CAF50; color: white; }\n");
      writer.write("tr:nth-child(even) { background-color: #f2f2f2; }\n");
      writer.write("</style>\n</head>\n<body>\n");
      writer.write("<h2 style='text-align:center;'>LLM Response Evaluation Report</h2>\n");
      writer.write("<table id='reportTable'>\n");
      writer.write(
          "<tr><th>User Input</th><th>Expected Response</th><th>Actual Response</th><th>Response Score</th><th>Response in Correct Format</th><th>Final Feedback</th></tr>\n");
      writer.flush();
      logger.info("Report initialized successfully.");
    } catch (IOException e) {
      logger.error("Failed to initialize report file: {}", REPORT_PATH, e);
    }
  }

  public static void appendScenarioResult(String userRequest, String expectedResponse,
      String actualResponse, Double score, Boolean correctFormat, String feedBack) {

    try (FileWriter writer = new FileWriter(REPORT_PATH, true)) {

      if ("Fail - Response deviates in meaning or detail.".equals(feedBack)) {
        writer.write("<tr style='background-color:#f8d7da;'>");
        logger.debug("Scenario marked as FAIL (highlighted in red).");
      } else {
        writer.write("<tr>");
        logger.debug("Scenario marked as PASS.");
      }
      writer.write("<td>" + escapeHtml(userRequest) + "</td>");
      writer.write("<td>" + escapeHtml(expectedResponse) + "</td>");
      writer.write("<td>" + escapeHtml(actualResponse) + "</td>");
      writer.write("<td>" + score + "</td>");
      writer.write("<td>" + correctFormat + "</td>");
      writer.write("<td>" + feedBack + "</td>");
      writer.write("</tr>\n");
      writer.flush();
      logger.info("Scenario successfully added to report.");
    } catch (IOException e) {
      logger.error("Error appending scenario to report.", e);
    }
  }


  public static void finalizeReport() {
    logger.info("Finalizing report and closing HTML tags.");
    try (FileWriter writer = new FileWriter(REPORT_PATH, true)) {
      writer.write("</table>\n</body>\n</html>");
      writer.flush();
      logger.info("Report finalized successfully at: {}", REPORT_PATH);
    } catch (IOException e) {
      logger.error("Failed to finalize HTML report.", e);
    }
  }

  private static String escapeHtml(String input) {
    return input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
  }
}