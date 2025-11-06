package org.northBayTest.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class HtmlReportGenerator {

  private static final String REPORT_PATH = "llmEvaluationReport.html";
  private static final AtomicInteger scenarioCounter = new AtomicInteger(
      0);


  public static void initializeReport() {
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
          "<tr><th>Scenario #</th><th>User Input</th><th>Expected Response</th><th>Actual Response</th><th>Response Score</th><th>Response in Correct Format</th><th>Final Feedback</th></tr>\n");
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void appendScenarioResult(String userRequest, String expectedResponse,
      String actualResponse, Double score, Boolean correctFormat, String feedBack) {
    int scenarioNumber = scenarioCounter.incrementAndGet();

    try (FileWriter writer = new FileWriter(REPORT_PATH, true)) {
      // Apply light red background if feedback matches condition
      if ("Response deviates in meaning or detail.".equals(feedBack)) {
        writer.write("<tr style='background-color:#f8d7da;'>");
      } else {
        writer.write("<tr>");
      }
      writer.write("<td>" + scenarioNumber + "</td>");
      writer.write("<td>" + escapeHtml(userRequest) + "</td>");
      writer.write("<td>" + escapeHtml(expectedResponse) + "</td>");
      writer.write("<td>" + escapeHtml(actualResponse) + "</td>");
      writer.write("<td>" + score + "</td>");
      writer.write("<td>" + correctFormat + "</td>");
      writer.write("<td>" + escapeHtml(feedBack) + "</td>");
      writer.write("</tr>\n");
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public static void finalizeReport() {
    try (FileWriter writer = new FileWriter(REPORT_PATH, true)) {
      writer.write("</table>\n</body>\n</html>");
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
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