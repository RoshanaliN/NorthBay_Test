# NorthBay Test Automation Suite

This project contains automated test scenarios for regression, UI validation, and LLM evaluation.

---

## How to Run Tests

### 1. Run All Regression Tests
To execute the complete regression suite:
```bash
./gradlew test
```
You can run a specific group of tests using Cucumber tags:
```bash
./gradlew test -Dcucumber.filter.tags="@mobile"
```
 ### 2. UI Test Execution Report
After running UI tests, the HTML report can be found at:
```bash
build/courgette-report/index.html
```
### 3. LLM Evaluation Report
Results for LLM prompt-response evaluation are generated at:
```bash
llmEvaluationReport.html
```

## Author: Roshanali Narsindani
## Frameworks Used: Cucumber, JUnit, Courgette, Selenium, Gradle